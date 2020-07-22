package org.jetbrains.research.refactorinsight.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.vcs.log.ui.VcsLogInternalDataKeys;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.services.WindowService;

/**
 * This is the Show Refactorings Action.
 * Adds a toggle button in the git tool window,
 * toggles between classical diff view and refactoring view.
 * Invokes the added git ui.
 */
public class ToggleRefactoringViewAction extends ToggleAction {

  @Override
  public void setSelected(@NotNull AnActionEvent e, boolean state) {
    WindowService.getInstance(e.getProject()).setSelected(e.getData(VcsLogInternalDataKeys.MAIN_UI), state);
  }

  @Override
  public boolean isSelected(@NotNull AnActionEvent e) {
    return WindowService.getInstance(e.getProject()).isSelected(e.getData(VcsLogInternalDataKeys.MAIN_UI));
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    e.getPresentation().setVisible(true);
    super.update(e);
  }
}