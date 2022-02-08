package org.jetbrains.research.refactorinsight.ui.windows;

import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffContentFactoryEx;
import com.intellij.diff.DiffManager;
import com.intellij.diff.DiffRequestPanel;
import com.intellij.diff.chains.DiffRequestProducerException;
import com.intellij.diff.comparison.ComparisonManagerImpl;
import com.intellij.diff.comparison.InnerFragmentsPolicy;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.fragments.LineFragment;
import com.intellij.diff.fragments.LineFragmentImpl;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.tools.util.text.LineOffsets;
import com.intellij.diff.tools.util.text.LineOffsetsUtil;
import com.intellij.diff.util.DiffUserDataKeys;
import com.intellij.diff.util.DiffUserDataKeysEx;
import com.intellij.diff.util.Range;
import com.intellij.diff.util.Side;
import com.intellij.ide.highlighter.JavaClassFileType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.Gray;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.JBTable;
import com.intellij.vcs.log.VcsFullCommitDetails;
import com.intellij.vcs.log.VcsLogProvider;
import com.intellij.vcs.log.data.VcsLogData;
import com.intellij.vcs.log.impl.VcsLogManager;
import com.intellij.vcs.log.impl.VcsProjectLog;
import icons.RefactorInsightIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.research.refactorinsight.RefactorInsightBundle;
import org.jetbrains.research.refactorinsight.adapters.CodeChange;
import org.jetbrains.research.refactorinsight.utils.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.intellij.util.containers.ContainerUtil.getFirstItem;
import static com.intellij.vcs.log.util.VcsLogUtil.getDetails;
import static java.util.Collections.singletonList;
import static org.codetracker.change.Change.Type.*;

public class ChangeHistoryToolbar implements Disposable {
    private ToolWindowManager toolWindowManager;
    private ToolWindow toolWindow;
    private Project project;
    private ElementType type;
    private DiffRequestPanel myDiffPanel;
    private ConcurrentHashMap<String, VcsFullCommitDetails> commitsDetails = new ConcurrentHashMap<>();
    private static final Logger LOG = Logger.getInstance(ChangeHistoryToolbar.class);

    public ChangeHistoryToolbar(Project project) {
        this.project = project;
        toolWindowManager = ToolWindowManager.getInstance(project);
        Utils.manager = toolWindowManager;
        toolWindow =
                toolWindowManager.registerToolWindow(RefactorInsightBundle.message("change.history.toolwindow.name"),
                        true, ToolWindowAnchor.BOTTOM);
        myDiffPanel = DiffManager.getInstance().createRequestPanel(project, this, null);
    }

    public void showToolbar(String objectName, ElementType type, List<CodeChange> methodsHistory) {
        this.type = type;
        //TODO: split the panels vertically?
        JBSplitter splitter = new JBSplitter(false, "file.history.selection.diff.splitter.proportion", 0.5f);
        JBTable table = new JBTable();
        table.setDefaultEditor(Object.class, null);
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addColumn(RefactorInsightBundle.message("change.type.column.name"));
        model.addColumn(RefactorInsightBundle.message("change.date.column.name"));
        model.addColumn(RefactorInsightBundle.message("change.author.column.name"));
        methodsHistory.forEach(i -> model.addRow(new Object[]{i, i.getChangeDate(), i.getChangeAuthor()}));
        addMouseListener(splitter, table);
        setFirstComponent(methodsHistory.size(), splitter, table);
        setSecondComponent(splitter);
        showToolWindow(objectName, splitter);
    }

    private void setFirstComponent(int size, JBSplitter splitter, JBTable table) {
        JBScrollPane pane = new JBScrollPane(table);
        JBLabel label =
                new JBLabel(String.format(RefactorInsightBundle.message("change.detected.count"),
                        size, size > 1 ? "s" : "", type.toString().toLowerCase()));
        label.setForeground(Gray._105);
        pane.setColumnHeaderView(label);
        splitter.setFirstComponent(pane);
    }

    private void setSecondComponent(JBSplitter splitter) {
        final JBLabel component =
                new JBLabel(RefactorInsightBundle.message("change.show.diff"), SwingConstants.CENTER);
        component.setForeground(Gray._105);
        splitter.setSecondComponent(component);
    }

    private @NotNull
    SimpleDiffRequest createSimpleRequest(@Nullable Project project,
                                          Collection<Change> vcsChanges,
                                          CodeChange entityChange,
                                          ProgressIndicator indicator) throws DiffRequestProducerException {
        indicator.setIndeterminate(true);
        DiffContent[] contents = getVcsChangeForSelectedChange(vcsChanges, entityChange, project);
        assert contents != null;

        DiffContent beforeContent = contents[0] == null ? DiffContentFactory.getInstance().createEmpty() : contents[0];
        DiffContent afterContent = contents[2] == null ? DiffContentFactory.getInstance().createEmpty() : contents[2];

        SimpleDiffRequest request = new SimpleDiffRequest(entityChange.getChangeType().getTitle(),
                beforeContent, afterContent, entityChange.getLocationInfoBefore().getFilePath(),
                entityChange.getLocationInfoAfter().getFilePath());
        ElementType type = entityChange.getType();

        if (entityChange.getChangeType().equals(INTRODUCED) || entityChange.getChangeType().equals(CONTAINER_CHANGE)) {
            //highlight all changes in the file
            request.putUserData(DiffUserDataKeys.MASTER_SIDE, Side.RIGHT);
        } else if (entityChange.getChangeType().equals(MOVED) ||
                entityChange.getDescription().contains("Move") ||
                !entityChange.getLocationInfoBefore().getFilePath().equals(entityChange.getLocationInfoAfter().getFilePath())) {
            //show an entity in the original file and in the target file the entity was move to
            //in case of composite refactorings such as Rename and Move the type might be equal RENAME, so we need to check if description contains Move
            request.putUserData(DiffUserDataKeysEx.CUSTOM_DIFF_COMPUTER,
                    (text1, text2, policy, innerChanges, i)
                            -> {
                        int startLineBefore = entityChange.getLocationInfoBefore().getStartLine() - 1;
                        int endLineBefore = entityChange.getLocationInfoBefore().getEndLine();
                        int startLineAfter = entityChange.getLocationInfoAfter().getStartLine() - 1;
                        int endLineAfter = entityChange.getLocationInfoAfter().getEndLine();

                        InnerFragmentsPolicy fragmentsPolicy = innerChanges ? InnerFragmentsPolicy.WORDS : InnerFragmentsPolicy.NONE;
                        LineOffsets offsets1 = LineOffsetsUtil.create(text1);
                        LineOffsets offsets2 = LineOffsetsUtil.create(text2);

                        ComparisonManagerImpl comparisonManager = ComparisonManagerImpl.getInstanceImpl();
                        ArrayList<LineFragment> result = new ArrayList<>(comparisonManager.compareLinesInner(
                                new Range(startLineBefore, endLineBefore, startLineAfter, endLineAfter),
                                text1, text2, offsets1, offsets2, policy, fragmentsPolicy, indicator));

                        //if there are changes in method body -> highlight them, otherwise highlight full method body
                        return result.size() != 0 ? result : Collections.singletonList(
                                new LineFragmentImpl(
                                        entityChange.getLocationInfoBefore().getStartLine() - 1,
                                        entityChange.getLocationInfoBefore().getEndLine(),
                                        entityChange.getLocationInfoAfter().getStartLine() - 1,
                                        entityChange.getLocationInfoAfter().getEndLine(),
                                        entityChange.getLocationInfoBefore().getStartOffset(),
                                        entityChange.getLocationInfoBefore().getEndOffset(),
                                        entityChange.getLocationInfoAfter().getStartOffset(),
                                        entityChange.getLocationInfoAfter().getEndOffset()
                                ));
                    });
        } else {
            boolean isAnnotationChange = entityChange.getChangeType().equals(ANNOTATION_CHANGE);
            boolean isRenameChange = entityChange.getChangeType().equals(RENAME);
            boolean isVariableOrAttributeChange = type.equals(ElementType.ATTRIBUTE) || type.equals(ElementType.VARIABLE);
            //correct lines for fields, variables, and method signature to highlight only the lines that contain the corresponding change
            //calculate the diff only for the entity changes
            int lineCountToHighlightBefore = 0;
            int lineCountToHighlightAfter = 0;
            if (isRenameChange || isVariableOrAttributeChange) {
                lineCountToHighlightBefore += 1;
                lineCountToHighlightAfter += 1;
            }

            //correct lines to highlight the annotation change depending on its type -- add, remove, modify
            if (isAnnotationChange) {
                if (entityChange.getDescription().contains("Add")) {
                    lineCountToHighlightBefore -= 1;
                    lineCountToHighlightAfter += 1;
                } else if (entityChange.getDescription().contains("Remove")) {
                    lineCountToHighlightBefore += 1;
                    lineCountToHighlightAfter -= 1;
                } else if (entityChange.getDescription().contains("Modify")) {
                    lineCountToHighlightBefore += 1;
                    lineCountToHighlightAfter += 1;
                }
            }

            int startLineBefore = entityChange.getLocationInfoBefore().getStartLine() - lineCountToHighlightBefore;
            int endLineBefore = entityChange.getLocationInfoBefore().getEndLine();
            int startLineAfter = entityChange.getLocationInfoAfter().getStartLine() - lineCountToHighlightAfter;
            int endLineAfter = entityChange.getLocationInfoAfter().getEndLine();

            request.putUserData(DiffUserDataKeysEx.CUSTOM_DIFF_COMPUTER,
                    (text1, text2, policy, innerChanges, i)
                            -> {
                        InnerFragmentsPolicy fragmentsPolicy = innerChanges ? InnerFragmentsPolicy.WORDS : InnerFragmentsPolicy.NONE;
                        LineOffsets offsets1 = LineOffsetsUtil.create(text1);
                        LineOffsets offsets2 = LineOffsetsUtil.create(text2);

                        ComparisonManagerImpl comparisonManager = ComparisonManagerImpl.getInstanceImpl();
                        return new ArrayList<>(comparisonManager.compareLinesInner(
                                new Range(startLineBefore, endLineBefore, startLineAfter, endLineAfter),
                                text1, text2, offsets1, offsets2, policy, fragmentsPolicy, indicator));
                    });
        }
        return request;
    }

    private void addMouseListener(JBSplitter splitter, JBTable table) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    CodeChange selectedChange = getRowValue(table, 0);
                    if (selectedChange == null) {
                        return;
                    }
                    showDiffForChange(selectedChange, splitter);
                }
            }
        });
    }

    private CodeChange getRowValue(JBTable table, int column) {
        int row = table.getSelectedRow();
        return (CodeChange) table.getModel().getValueAt(row, column);
    }

    private void showDiffForChange(CodeChange entityChange, JBSplitter splitter) {
        VcsLogManager logManager = VcsProjectLog.getInstance(project).getLogManager();
        if (logManager == null) {
            return;
        }

        ProgressManager.getInstance().run(new Task.Modal(
                project, "Computing diff for a selected change", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                VcsLogData vcsLogData = VcsProjectLog.getInstance(project).getLogManager().getDataManager();
                VirtualFile root = vcsLogData.getRoots().iterator().next();
                VcsLogProvider vcsLogProvider = VcsProjectLog.getInstance(project).getDataManager().getLogProvider(root);
                try {
                    commitsDetails.put(entityChange.getCommitId(), getFirstItem(getDetails(vcsLogProvider, root, singletonList(entityChange.getCommitId()))));
                    final Collection<Change> vcsChanges = Optional.ofNullable(commitsDetails.get(entityChange.getCommitId()))
                            .map(VcsFullCommitDetails::getChanges).orElse(new ArrayList<>());
                    myDiffPanel.setRequest(createSimpleRequest(project, vcsChanges, entityChange, indicator));
                } catch (VcsException | DiffRequestProducerException e) {
                    LOG.error(String.format("[RefactorInsight]: Failed to compute diff. Details: %s", e.getMessage()), e);
                }
            }

            @Override
            public void onFinished() {
                splitter.setSecondComponent(myDiffPanel.getComponent());
                splitter.updateUI();
            }
        });
    }

    public static DiffContent[] getVcsChangeForSelectedChange(Collection<Change> changes,
                                                              CodeChange entityChange,
                                                              Project project) {
        try {
            DiffContentFactoryEx myDiffContentFactory = DiffContentFactoryEx.getInstanceEx();
            DiffContent[] contents = {null, null, null};
            for (Change change : changes) {
                if (change.getBeforeRevision() != null) {
                    if (change.getBeforeRevision().getFile().getPath().contains(entityChange.getLocationInfoBefore().getFilePath())) {
                        contents[0] = myDiffContentFactory.create(project,
                                change.getBeforeRevision().getContent(),
                                JavaClassFileType.INSTANCE);
                    }
                }
                if (change.getAfterRevision() != null
                        && change.getAfterRevision().getFile().getPath().contains(entityChange.getLocationInfoAfter().getFilePath())) {
                    contents[2] = myDiffContentFactory.create(project,
                            change.getAfterRevision().getContent(),
                            JavaClassFileType.INSTANCE);
                }
            }
            if (contents[0] != null || contents[2] != null) {
                return contents;
            }
        } catch (VcsException ex) {
            LOG.error(String.format("[RefactorInsight]: Failed to get the corresponding VCS change." +
                    " Details: %s", ex.getMessage()), ex);
        }
        return null;
    }

    private void showToolWindow(String methodName, JComponent table) {
        Content content;
        if ((content = toolWindow.getContentManager().findContent(methodName)) != null) {
            content.setComponent(table);
        } else {
            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            content = contentFactory.createContent(table, methodName, false);
            toolWindow.getContentManager().addContent(content);
        }

        toolWindow.getContentManager().setSelectedContent(content);
        toolWindow.setIcon(RefactorInsightIcons.toolWindow);
        toolWindow.show();
    }

    @Override
    public void dispose() {

    }
}
