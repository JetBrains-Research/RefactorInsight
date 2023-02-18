package org.jetbrains.research.refactorinsight.actions;

import com.intellij.diff.FrameDiffTool;
import com.intellij.diff.tools.util.DiffDataKeys;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.ex.CheckboxAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.folding.ImportFolder;
import org.jetbrains.research.refactorinsight.folding.RefactoringFolder;

public class HideNonFunctionalChangesAction extends CheckboxAction {
    boolean hide = true;

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return hide;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        hide = state;
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        editor.getFoldingModel().runBatchFoldingOperation(() -> {
            for (FoldRegion foldRegion : editor.getFoldingModel().getAllFoldRegions()) {
                if (RefactoringFolder.isRefactoringFoldRegion(foldRegion) ||
                        ImportFolder.isImportFoldRegion(foldRegion)) {
                    foldRegion.setExpanded(!hide);
                }
            }
        });
    }
}
