package org.jetbrains.research.refactorinsight.actions;

import com.intellij.diff.FrameDiffTool;
import com.intellij.diff.tools.simple.SimpleDiffViewer;
import com.intellij.diff.tools.util.DiffDataKeys;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ex.CheckboxAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.folding.ImportFolder;
import org.jetbrains.research.refactorinsight.folding.RefactoringFolder;

public class HideNonFunctionalChangesAction extends CheckboxAction {
    static private boolean hide = true;

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

    public static boolean isHide() {
        return hide;
    }
}
