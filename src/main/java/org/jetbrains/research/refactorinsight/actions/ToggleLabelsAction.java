package org.jetbrains.research.refactorinsight.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareToggleAction;
import com.intellij.vcs.log.ui.VcsLogInternalDataKeys;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.services.WindowService;

/**
 * Toggles the labeling of commits that contain refactorings in VCSTable.
 */
public class ToggleLabelsAction extends DumbAwareToggleAction {

  @Override
  public boolean isSelected(@NotNull AnActionEvent e) {
    return WindowService.getInstance(e.getProject()).isLabelsVisible(e);
  }

  @Override
  public void setSelected(@NotNull AnActionEvent e, boolean state) {
    WindowService.getInstance(e.getProject()).setLabelsVisible(e, state);
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    e.getPresentation().setEnabledAndVisible(e.getProject() != null
                                                 && e.getData(VcsLogInternalDataKeys.MAIN_UI) != null);
    super.update(e);
  }
}
