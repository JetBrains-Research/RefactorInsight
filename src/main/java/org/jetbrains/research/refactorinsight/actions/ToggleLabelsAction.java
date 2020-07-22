package org.jetbrains.research.refactorinsight.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.services.WindowService;

public class ToggleLabelsAction extends ToggleAction {

  @Override
  public boolean isSelected(@NotNull AnActionEvent e) {
    return WindowService.getInstance(e.getProject()).isLabelsVisible(e);
  }

  @Override
  public void setSelected(@NotNull AnActionEvent e, boolean state) {
    WindowService.getInstance(e.getProject()).setLabelsVisible(e, state);
  }
}
