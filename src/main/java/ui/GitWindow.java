package ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import org.jetbrains.annotations.NotNull;
import services.WindowService;

public class GitWindow extends ToggleAction {

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