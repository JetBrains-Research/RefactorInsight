package org.jetbrains.research.refactorinsight.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.DumbAwareToggleAction;
import com.intellij.openapi.project.Project;
import com.intellij.vcs.log.ui.MainVcsLogUi;
import com.intellij.vcs.log.ui.VcsLogInternalDataKeys;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.services.WindowService;

/**
 * This is the Show Refactorings Action.
 * Adds a toggle button in the git tool window,
 * toggles between classical diff view and refactoring view.
 * Invokes the added git ui.
 */
public class GitLogToggleRefactoringViewAction extends DumbAwareToggleAction {

  @Override
  public void setSelected(@NotNull AnActionEvent e, boolean state) {
    Project project = e.getRequiredData(PlatformDataKeys.PROJECT);
    MainVcsLogUi vcsLogUi = e.getRequiredData(VcsLogInternalDataKeys.MAIN_UI);
    WindowService.getInstance(project).setSelected(vcsLogUi, state);
  }

  @Override
  public boolean isSelected(@NotNull AnActionEvent e) {
    if (!isEnabled(e)) {
      return false;
    }
    Project project = e.getRequiredData(PlatformDataKeys.PROJECT);
    MainVcsLogUi vcsLogUi = e.getRequiredData(VcsLogInternalDataKeys.MAIN_UI);
    return WindowService.getInstance(project).isSelected(vcsLogUi);
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    e.getPresentation().setEnabledAndVisible(isEnabled(e));
    WindowService.getInstance(e.getProject()).update(e);
    super.update(e);
  }

  private boolean isEnabled(@NotNull AnActionEvent e) {
    return e.getProject() != null && e.getData(VcsLogInternalDataKeys.MAIN_UI) != null;
  }
}