package actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import org.jetbrains.annotations.NotNull;
import services.WindowService;

/**
 * This is the Show Refactorings Action.
 * Adds a toggle button in the git tool window,
 * toggles between classical diff view and refactoring view.
 * Invokes the added git ui.
 */
public class ToggleRefactoringViewAction extends ToggleAction {

  @Override
  public void setSelected(@NotNull AnActionEvent e, boolean state) {
    WindowService.getInstance(e.getProject()).setSelected(e, state);
  }

  @Override
  public boolean isSelected(@NotNull AnActionEvent e) {
    return WindowService.getInstance(e.getProject()).isSelected(e);
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    e.getPresentation().setVisible(true);
    super.update(e);
  }
}