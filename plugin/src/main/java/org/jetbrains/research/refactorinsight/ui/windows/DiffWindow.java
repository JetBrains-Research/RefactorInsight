package org.jetbrains.research.refactorinsight.ui.windows;

import com.intellij.diff.*;
import com.intellij.diff.chains.DiffRequestChain;
import com.intellij.diff.chains.SimpleDiffRequestChain;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.contents.DocumentContent;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.diff.tools.simple.*;
import com.intellij.diff.tools.util.base.DiffViewerListener;
import com.intellij.diff.tools.util.base.IgnorePolicy;
import com.intellij.diff.tools.util.side.TwosideTextDiffViewer;
import com.intellij.diff.util.DiffUserDataKeysEx;
import com.intellij.ide.highlighter.JavaClassFileType;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TitlePanel;
import com.intellij.openapi.ui.WindowWrapper;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.changes.actions.diff.ChangeDiffRequestProducer;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.panels.Wrapper;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.diff.MoreSidedDiffRequestGenerator.MoreSidedRange;
import org.jetbrains.research.refactorinsight.diff.ThreeSidedRange;
import org.jetbrains.research.refactorinsight.folding.RefactoringFolder;
import org.jetbrains.research.refactorinsight.ui.Keys;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.jetbrains.research.refactorinsight.utils.Utils.fixPath;

/**
 * Deals with refactoring diff requests.
 * Generates ui components and disposes them.
 * Ui components are generated by fetching the right file revisions and adjusting
 * the existing diff viewer components.
 */
public class DiffWindow extends DiffExtension {

    /**
     * Requests diff window to show specific refactoring with two editors.
     *
     * @param info    RefactoringInfo
     * @param project Current project
     */
    public static void showDiff(Collection<Change> changes, RefactoringInfo info,
                                Project project, List<RefactoringInfo> refactoringInfos) {
        final Predicate<RefactoringInfo> showable =
                i -> !i.isHidden() && i.getLeftPath() != null;
        List<DiffRequest> requests = refactoringInfos.stream()
                .filter(showable)
                .map(i -> i.generate(getDiffContents(changes, i, project)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        final int index = refactoringInfos.stream()
                .filter(showable).collect(Collectors.toList()).indexOf(info);
        if (index != -1) {
            DiffRequestChain chain = new SimpleDiffRequestChain(requests, index);
            chain.putUserData(DiffUserDataKeysEx.FORCE_DIFF_TOOL, SimpleDiffTool.INSTANCE);
            ExternalSystemApiUtil.executeOnEdt(false, () ->
                    DiffManager.getInstance().showDiff(project, chain,
                            new DiffDialogHints(WindowWrapper.Mode.FRAME))
            );
        }
    }

    private static DiffContent[] getDiffContents(Collection<Change> changes,
                                                 RefactoringInfo info, Project project) {
        if (info.getLeftPath() == null || info.getRightPath() == null) {
            return null;
        }
        return info.isMoreSided() ? getMoreSidedDiffContents(changes, info, project) :
                getStandardDiffContents(changes, info, project);
    }

    /**
     * This method is for "More Sided" refactoring diff.
     */
    private static DiffContent[] getMoreSidedDiffContents(Collection<Change> changes,
                                                          RefactoringInfo info, Project project) {
        try {
            DiffContentFactoryEx myDiffContentFactory = DiffContentFactoryEx.getInstanceEx();
            ArrayList<DiffContent> contentList = new ArrayList<>();
            for (Change change : changes) {
                if (change.getAfterRevision() != null
                        && change.getAfterRevision().getFile().getPath().contains(info.getRightPath())) {
                    contentList.add(myDiffContentFactory
                            .create(project, change.getAfterRevision().getContent(),
                                    JavaClassFileType.INSTANCE));
                    break;
                }
            }
            for (Pair<String, Boolean> pathPair : info.getMoreSidedLeftPaths()) {
                for (Change change : changes) {
                    ContentRevision revision = pathPair.second
                            ? change.getAfterRevision() : change.getBeforeRevision();
                    if (revision != null
                            && revision.getFile().getPath().contains(fixPath(pathPair.first))) {
                        contentList.add(myDiffContentFactory
                                .create(project, revision.getContent(),
                                        JavaClassFileType.INSTANCE));
                        break;
                    }
                }
            }
            return contentList.toArray(new DiffContent[0]);
        } catch (VcsException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * This is contents getter is for standard two or three sided refactoring diff.
     */
    private static DiffContent[] getStandardDiffContents(Collection<Change> changes,
                                                         RefactoringInfo info, Project project) {
        try {
            DiffContentFactoryEx myDiffContentFactory = DiffContentFactoryEx.getInstanceEx();
            DiffContent[] contents = {null, null, null};
            for (Change change : changes) {
                if (change.getBeforeRevision() != null) {
                    if (change.getBeforeRevision().getFile().getPath().contains(info.getLeftPath())) {
                        contents[0] = myDiffContentFactory.create(project,
                                change.getBeforeRevision().getContent(),
                                JavaClassFileType.INSTANCE);
                    }
                }
                if (info.isThreeSided()
                        && change.getAfterRevision() != null
                        && change.getAfterRevision().getFile().getPath().contains(info.getMidPath())) {
                    contents[1] = myDiffContentFactory.create(project,
                            change.getAfterRevision().getContent(),
                            JavaClassFileType.INSTANCE);
                }
                if (change.getAfterRevision() != null
                        && change.getAfterRevision().getFile().getPath().contains(info.getRightPath())) {
                    contents[2] = myDiffContentFactory.create(project,
                            change.getAfterRevision().getContent(),
                            JavaClassFileType.INSTANCE);
                }
            }
            return contents;

        } catch (VcsException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * IntelliJ Diff Extension.
     * This is needed to obtain the viewer object.
     * Sets a listener which is activated once classical diff is calculated and
     * code ranges can be replaced with refactoring specific ranges.
     */
    @Override
    public void onViewerCreated(@NotNull FrameDiffTool.DiffViewer viewer,
                                @NotNull DiffContext context, @NotNull DiffRequest request) {
        putCommitIdData(viewer, request);

        //Check diff viewer type for refactoring
        Boolean isRefactoring = request.getUserData(Keys.REFACTORING);

        //Fold Move Method refactorings in the base diff that contains the information about all changes in the file.
        if (isRefactoring == null) {
            RefactoringFolder.foldRefactorings(viewer, request);
            return;
        }

        //Check if diff viewer is three sided
        List<ThreeSidedRange> threeSidedRanges = request.getUserData(Keys.THREESIDED_RANGES);
        if (threeSidedRanges != null) {
            SimpleThreesideDiffViewer myViewer = (SimpleThreesideDiffViewer) viewer;
            myViewer.getTextSettings().setExpandByDefault(false);
            myViewer.addListener(new MyThreeSidedDiffViewerListener(myViewer, threeSidedRanges));
            return;
        }

        //Check if diff viewer is more sided
        List<MoreSidedRange> moreSidedRanges =
                request.getUserData(Keys.MORESIDED_RANGES);
        SimpleDiffViewer myViewer = (SimpleDiffViewer) viewer;

        //Set diff window settings and hide
        try {
            myViewer.getTextSettings().setIgnorePolicy(IgnorePolicy.DEFAULT);
        } catch (IllegalStateException ignored) {
            //
        }

        myViewer.addListener(new MyTwoSidedDiffViewerListener(myViewer));

        if (moreSidedRanges != null) {
            myViewer.getTextSettings().setExpandByDefault(true);
            //Sort on filename and code ranges.
            List<MoreSidedRange> sortedRanges =
                    new ArrayList<>(moreSidedRanges);
            Collections.sort(sortedRanges);

            //Highlight right part of diff window
            //  Get diff colors of current theme.
            EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
            TextAttributes lineColors = scheme.getAttributes(TextAttributesKey.find("DIFF_MODIFIED"));
            TextAttributes offsetColors = scheme.getAttributes(TextAttributesKey.find("DIFF_INSERTED"));
            sortedRanges.forEach(moreSidedRange -> {
                //  Highlight lines
                if (moreSidedRange.startLineRight != -1 && moreSidedRange.endLineRight != -1) {
                    for (int i = moreSidedRange.startLineRight - 1; i < moreSidedRange.endLineRight; i++) {
                        myViewer.getEditor2().getMarkupModel().addLineHighlighter(i, 2, lineColors);
                    }
                    //  Highlight offsets
                    myViewer.getEditor2().getMarkupModel()
                            .addRangeHighlighter(moreSidedRange.startOffsetRight, moreSidedRange.endOffsetRight,
                                    3, offsetColors, HighlighterTargetArea.EXACT_RANGE);
                }
            });

            //Generate list for left side of diff window
            List<Pair<MoreSidedRange, Project>> pairs = new ArrayList<>();
            String current = "";
            for (MoreSidedRange moreSidedRange : sortedRanges) {
                //If new file add title row first by setting project null (this is tested in renderer)
                if (!moreSidedRange.leftPath.equals(current)) {
                    current = moreSidedRange.leftPath;
                    pairs.add(new Pair<>(moreSidedRange, null));
                }
                pairs.add(new Pair<>(moreSidedRange, myViewer.getProject()));
            }


            //Generate Left Side UI
            JBList<Pair<MoreSidedRange, Project>> editorList =
                    new JBList<>(JBList.createDefaultListModel(pairs));
            MoreSidedRenderer renderer = new MoreSidedRenderer(editorList.getItemsCount());
            Disposer.register(myViewer, renderer);
            editorList.setCellRenderer(renderer);
            JPanel leftPanel = (JPanel) myViewer.getEditor1().getComponent();
            leftPanel.remove(0);
            leftPanel.add(new JBScrollPane(editorList));
            return;
        }

        // Assume diff viewer is two-sided
        myViewer.getTextSettings().setExpandByDefault(false);
    }

    private void putCommitIdData(FrameDiffTool.DiffViewer viewer, @NotNull DiffRequest request) {
        if (viewer instanceof TwosideTextDiffViewer) {
            Change change = request.getUserData(ChangeDiffRequestProducer.CHANGE_KEY);
            if (change != null && change.getBeforeRevision() != null && change.getAfterRevision() != null) {
                // revision before changes
                ((TwosideTextDiffViewer) viewer).getEditor1().getVirtualFile()
                        .putUserData(Keys.COMMIT_ID, change.getBeforeRevision().getRevisionNumber().asString());
                // revision after changes
                ((TwosideTextDiffViewer) viewer).getEditor2().getVirtualFile()
                        .putUserData(Keys.COMMIT_ID, change.getAfterRevision().getRevisionNumber().asString());
                ((TwosideTextDiffViewer) viewer).getEditor1().getVirtualFile()
                        .putUserData(Keys.CHILD_COMMIT_ID, change.getAfterRevision().getRevisionNumber().asString());
            }
        }
    }

    /**
     * Renders the editors and title rows on the left side of diff window.
     */
    public static class MoreSidedRenderer implements Disposable,
            ListCellRenderer<Pair<MoreSidedRange, Project>> {

        TitlePanel[] titles;
        Editor[] editors;

        public MoreSidedRenderer(int size) {
            titles = new TitlePanel[size];
            editors = new Editor[size];
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends Pair<MoreSidedRange, Project>> jlist,
                Pair<MoreSidedRange, Project> pair, int i, boolean b,
                boolean b1) {
            //is title panel
            if (pair.second == null) {
                if (titles[i] == null) {
                    titles[i] = new TitlePanel(pair.first.leftPath, null);
                }
                return titles[i];
            } else if (editors[i] == null) {
                generateEditor(i, pair);
            }
            editors[i].getScrollingModel().scrollVertically(
                    Math.max(pair.first.startLineLeft - 2, 0) * editors[i].getLineHeight());
            return editors[i].getComponent();

        }

        /**
         * Generate Editor UI for left side of diffwindow.
         *
         * @param i    Index of row in left side
         * @param pair RangeData and Project
         */
        public void generateEditor(int i, Pair<MoreSidedRange, Project> pair) {

            //Instantiate editor
            DocumentContent content = (DocumentContent) pair.first.content;
            Editor editor = EditorFactory.getInstance().createEditor(content.getDocument(),
                    pair.second, JavaFileType.INSTANCE, true);

            //Get highlight colors corresponding with theme and revision
            EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
            TextAttributes lineColor = scheme.getAttributes(TextAttributesKey.find("DIFF_MODIFIED"));
            TextAttributes offsetColor
                    = pair.first.startLineRight == -1 && pair.first.endLineRight == -1
                    ? scheme.getAttributes(TextAttributesKey.find("DIFF_INSERTED"))
                    : scheme.getAttributes(TextAttributesKey.find("DIFF_DELETED"));

            //Highlighting process
            //  Hide caret line highlighting
            editor.getSettings().setCaretRowShown(false);
            //  Highlight lines
            for (int ind = pair.first.startLineLeft - 1; ind < pair.first.endLineLeft; ind++) {
                editor.getMarkupModel().addLineHighlighter(ind, 2, lineColor);
            }
            //  Highlight offsets
            editor.getMarkupModel().addRangeHighlighter(pair.first.startOffsetLeft,
                    pair.first.endOffsetLeft, 10, offsetColor,
                    HighlighterTargetArea.EXACT_RANGE);

            //Set editor size to fit coderange
            int editorSize = editor.getLineHeight()
                    * (pair.first.endLineLeft - Math.max(pair.first.startLineLeft - 1, 1) + 2);
            editor.getComponent().setPreferredSize(new Dimension(400, editorSize));


            //Hide scrollbar
            ((EditorImpl) editor).getScrollPane().getVerticalScrollBar()
                    .setPreferredSize(new Dimension(0, 0));
            editors[i] = editor;
        }


        @Override
        public void dispose() {
            for (Editor editor : editors) {
                if (editor != null) {
                    EditorFactory.getInstance().releaseEditor(editor);
                }
            }
        }
    }

    /**
     * Hides diff settings which might crash the refactorings diff viewer.
     * Should be called onAfterRediff.
     *
     * @param diffViewerComponent Component from diff viewer
     */
    public static void hideToolbarActions(Component diffViewerComponent) {
        BorderLayoutPanel panel =
                (BorderLayoutPanel) diffViewerComponent.getParent().getParent().getParent().getComponent(0);
        Wrapper wrapper = (Wrapper) panel.getComponent(0);
        ActionToolbarImpl toolbar = (ActionToolbarImpl) wrapper.getComponent(0);
        toolbar.addContainerListener(new ContainerListener() {
            @Override
            public void componentAdded(ContainerEvent containerEvent) {
                if (containerEvent.getChild() instanceof JPanel) {
                    containerEvent.getChild().setVisible(false);
                }
            }

            @Override
            public void componentRemoved(ContainerEvent containerEvent) {

            }
        });
    }

    public static class MyTwoSidedDiffViewerListener extends DiffViewerListener {

        private final SimpleDiffViewer viewer;

        public MyTwoSidedDiffViewerListener(SimpleDiffViewer viewer) {
            this.viewer = viewer;
        }

        @Override
        protected void onAfterRediff() {
            super.onAfterRediff();
            hideToolbarActions(viewer.getComponent());
        }
    }


    public static class MyThreeSidedDiffViewerListener extends DiffViewerListener {

        private final SimpleThreesideDiffViewer viewer;
        private final List<ThreeSidedRange> ranges;

        /**
         * EventListener for DiffWindow finishing diff calculation.
         *
         * @param viewer DiffViewer
         * @param ranges List of ThreeSidedRanges
         */
        public MyThreeSidedDiffViewerListener(SimpleThreesideDiffViewer viewer,
                                              List<ThreeSidedRange> ranges) {
            this.ranges = ranges;
            this.viewer = viewer;
        }

        @Override
        protected void onAfterRediff() {
            List<SimpleThreesideDiffChange> oldMarkings = viewer.getChanges();
            oldMarkings.forEach(ThreesideDiffChangeBase::destroy);
            oldMarkings.clear();
            oldMarkings.addAll(ranges.stream()
                    .map(r -> r.getDiffChange(viewer))
                    .collect(Collectors.toList())
            );
            hideToolbarActions(viewer.getComponent());
        }
    }
}
