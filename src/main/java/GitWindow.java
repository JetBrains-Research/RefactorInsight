import com.intellij.diff.DiffContentFactoryEx;
import com.intellij.diff.DiffManager;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.diff.impl.mergeTool.DiffRequestFactoryImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ui.ChangesTree;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBViewport;
import com.intellij.vcs.log.CommitId;
import com.intellij.vcs.log.Hash;
import com.intellij.vcs.log.VcsFullCommitDetails;
import com.intellij.vcs.log.ui.MainVcsLogUi;
import com.intellij.vcs.log.ui.VcsLogInternalDataKeys;
import com.intellij.vcs.log.ui.frame.VcsLogChangesBrowser;
import com.intellij.vcs.log.ui.table.VcsLogGraphTable;
import com.intellij.vcs.log.util.VcsLogUtil;
import com.intellij.vcsUtil.VcsUtil;
import git4idea.changes.GitChangeUtils;
import git4idea.history.GitHistoryUtils;
import gr.uom.java.xmi.diff.CodeRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.ls.LSOutput;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class GitWindow extends ToggleAction {

    private ChangesTree changesTree;
    private JBViewport viewport;
    private boolean selected = false;
    private VcsLogGraphTable table;
    private JBLabel test;
    private JBScrollPane scrollPane;
    private MiningService miningService;
    Project project;
    AnActionEvent event;
    DiffContentFactoryEx myDiffContentFactory;


    private void setUp(@NotNull AnActionEvent e) {
        VcsLogChangesBrowser changesBrowser = (VcsLogChangesBrowser) e.getData(VcsLogChangesBrowser.DATA_KEY);
        MainVcsLogUi logUI = e.getData(VcsLogInternalDataKeys.MAIN_UI);

        project = e.getProject();

        miningService = project.getService(MiningService.class);

        table = logUI.getTable();
        table.getSelectionModel().addListSelectionListener(new CommitSelectionListener());
        event = e;

        myDiffContentFactory = DiffContentFactoryEx.getInstanceEx();

        changesTree = changesBrowser.getViewer();
        viewport = (JBViewport) changesTree.getParent();
        test = new JBLabel("TEST LABEL");
        test.setVerticalAlignment(JBLabel.CENTER);
        scrollPane = new JBScrollPane(test);
    }

    private void toRefactoringView(@NotNull AnActionEvent e) {
        System.out.println("Button ON");
        viewport.setView(scrollPane);
    }

    private void toChangesView(@NotNull AnActionEvent e) {
        viewport.setView(changesTree);
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return selected;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        if (changesTree == null) setUp(e);
        if (state) {
            toRefactoringView(e);
        } else {
            toChangesView(e);
        }
        selected = state;
    }

    class CommitSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent listSelectionEvent) {
            if (listSelectionEvent.getValueIsAdjusting()) return;
            DefaultListSelectionModel selectionModel = (DefaultListSelectionModel) listSelectionEvent.getSource();

            int beginIndex = selectionModel.getMinSelectionIndex();
            int endIndex = selectionModel.getMaxSelectionIndex();

            if (beginIndex != -1 || endIndex != -1) {
                scrollPane.getViewport().setView(buildList(beginIndex));
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setVisible(true);
        e.getProject().getService(MiningService.class).loaded();
        super.update(e);
    }
    private JBList buildList(int index){
        String commitId = table.getModel().getCommitId(index).getHash().asString();

        List<RefactoringInfo> refs = miningService.getRefactorings(commitId)
                .stream()
                .map(s -> RefactoringInfo.fromString(s))
                .collect(Collectors.toList());

        String[] names = refs.stream()
                .map(r -> r != null ? r.getName() : "not mined, DON'T CLICK!!")
                .toArray(String[]::new);

        JBList<String> list = new JBList<>(names);

        MouseAdapter mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showDiff(index, refs.get(list.locationToIndex(e.getPoint())));
                }
            }
        };
        list.addMouseListener(mouseListener);
        return list;
    }

    private void showDiff(int index, RefactoringInfo ri){

        List<String> leftPaths = ri.getLeftSide()
                .stream()
                .map(cr -> project.getBasePath() + "/" + cr.getFilePath())
                .collect(Collectors.toList());
        List<String> rightPaths = ri.getRightSide()
                .stream()
                .map(cr -> project.getBasePath() + "/" + cr.getFilePath())
                .collect(Collectors.toList());

        table.getModel().getFullDetails(index).getChanges()
                .stream()
                .filter(c -> leftPaths.contains(c.getBeforeRevision().getFile().getPath())
                        ||rightPaths.contains(c.getAfterRevision().getFile().getPath()))
                .forEach(change -> {
                    String contentBefore = null, contentAfter = null;
                    try {
                        contentBefore = change.getBeforeRevision().getContent();
                        contentAfter = change.getAfterRevision().getContent();
                    } catch (VcsException e) {
                        e.printStackTrace();
                    }
                    DiffContent d1 = contentBefore != null ? myDiffContentFactory.create(project, contentBefore)
                            : myDiffContentFactory.createEmpty();
                    DiffContent d2 = contentAfter != null ? myDiffContentFactory.create(project, contentAfter)
                            : myDiffContentFactory.createEmpty();
                    SimpleDiffRequest request = new SimpleDiffRequest("Refactorings", d1, d2,
                            change.getBeforeRevision().getFile().getPath(),
                            change.getAfterRevision().getFile().getPath());
                    DiffManager.getInstance().showDiff(project, request);
                });
    }


}