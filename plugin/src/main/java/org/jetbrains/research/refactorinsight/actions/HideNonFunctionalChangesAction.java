package org.jetbrains.research.refactorinsight.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.project.DumbAwareToggleAction;
import org.jetbrains.annotations.NotNull;

public class HideNonFunctionalChangesAction extends DumbAwareToggleAction {
    boolean hide = false;

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
                if (foldRegion.getPlaceholderText().equals("refactoring")) foldRegion.setExpanded(!hide);
            }
        });
    }
}
