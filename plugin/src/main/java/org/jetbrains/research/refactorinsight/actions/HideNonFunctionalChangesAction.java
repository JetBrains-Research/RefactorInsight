package org.jetbrains.research.refactorinsight.actions;

import com.intellij.diff.DiffContentFactoryImpl;
import com.intellij.diff.FrameDiffTool;
import com.intellij.diff.tools.simple.SimpleDiffViewer;
import com.intellij.diff.tools.util.DiffDataKeys;
import com.intellij.diff.util.Side;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ex.CheckboxAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.data.RefactoringEntry;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.folding.ImportFolder;
import org.jetbrains.research.refactorinsight.folding.RefactoringFolder;
import org.jetbrains.research.refactorinsight.services.MiningService;
import org.jetbrains.research.refactorinsight.ui.Keys;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HideNonFunctionalChangesAction extends CheckboxAction {
    static private boolean hide = true;
    static private Set<RangeHighlighter> refactoringHighlighters = new HashSet<>();

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return hide;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        hide = state;

        FrameDiffTool.DiffViewer viewer = e.getRequiredData(DiffDataKeys.DIFF_VIEWER);
        if (!(viewer instanceof SimpleDiffViewer diffViewer)) return;

        modifyEditor(diffViewer.getEditor1());
        modifyEditor(diffViewer.getEditor2());

        collectHighlighters(diffViewer.getEditor1(), Side.LEFT);
        collectHighlighters(diffViewer.getEditor2(), Side.RIGHT);

        modifyHighlighters(diffViewer.getEditor1());
        modifyHighlighters(diffViewer.getEditor2());
    }

    private void modifyEditor(@NotNull Editor editor) {
        editor.getFoldingModel().runBatchFoldingOperation(() -> {
            for (FoldRegion foldRegion : editor.getFoldingModel().getAllFoldRegions()) {
                if (RefactoringFolder.isRefactoringFoldRegion(foldRegion) ||
                        ImportFolder.isImportFoldRegion(foldRegion)) {
                    foldRegion.setExpanded(!hide);
                }
            }
        });
    }

    private void modifyHighlighters(@NotNull Editor editor) {
        for (RangeHighlighter highlighter : refactoringHighlighters) {
            if (hide) {
                editor.getMarkupModel().removeHighlighter(highlighter);
            } else {
                editor.getMarkupModel().addRangeHighlighter(
                        highlighter.getStartOffset(),
                        highlighter.getEndOffset(),
                        highlighter.getLayer(),
                        highlighter.getTextAttributes(editor.getColorsScheme()),
                        highlighter.getTargetArea());
            }
        }
    }

    private void collectHighlighters(@NotNull Editor editor, @NotNull Side side) {
        MarkupModel model = editor.getMarkupModel();
        RangeHighlighter[] highlighters = model.getAllHighlighters();
        List<RefactoringInfo> refactorings = getRefactoringInfos(editor, !side.isLeft());
        assert refactorings != null;
        for (RangeHighlighter highlighter : highlighters) {
            int startOffset = highlighter.getStartOffset();
            int lineNumber = editor.getDocument().getLineNumber(startOffset);
            for (RefactoringInfo info : refactorings) {
                if (info.containsElement(lineNumber, startOffset, !side.isLeft())) {
                    refactoringHighlighters.add(highlighter);
                }
            }
        }
    }

    private List<RefactoringInfo> getRefactoringInfos(@NotNull Editor editor, boolean isRight) {
        PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(editor.getDocument());
        VirtualFile virtualFile = psiFile.getVirtualFile();
        String commitId;
        final String DIFF_WINDOW_CLASS_NAME_PREFIX = DiffContentFactoryImpl.class.getName() + "$";
        if (virtualFile.getClass().getName().startsWith(DIFF_WINDOW_CLASS_NAME_PREFIX)) {
            if (isRight) commitId = virtualFile.getUserData(Keys.COMMIT_ID);
            else commitId = virtualFile.getUserData(Keys.CHILD_COMMIT_ID);
            if (commitId == null) return null;
            MiningService miner = MiningService.getInstance(editor.getProject());
            RefactoringEntry entry = miner.get(commitId);
            if (entry == null) return null;
            return entry.getRefactorings().stream().filter(ref -> fromSameFile(virtualFile, ref, isRight)).toList();
        }
        return null;
    }

    private boolean fromSameFile(VirtualFile virtualFile, RefactoringInfo refactoringInfo, boolean isRight) {
        String elementPath = virtualFile.getPath();
        String refactoringClassPath = isRight ? refactoringInfo.getRightPath() : refactoringInfo.getLeftPath();
        return elementPath.endsWith(refactoringClassPath);
    }

    public static boolean isHide() {
        return hide;
    }
}
