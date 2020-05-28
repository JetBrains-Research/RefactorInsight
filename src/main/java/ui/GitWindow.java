package ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.vcs.log.ui.VcsLogInternalDataKeys;
import com.intellij.vcs.log.ui.table.VcsLogGraphTable;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class GitWindow extends ToggleAction {

  private Map<VcsLogGraphTable, GitWindowInfo> gitInfo = new HashMap<>();
  private boolean state = false;

  @Override
  public void setSelected(@NotNull AnActionEvent e, boolean state) {
    VcsLogGraphTable table = e.getData(VcsLogInternalDataKeys.MAIN_UI).getTable();
    gitInfo.putIfAbsent(table, new GitWindowInfo(e));
    gitInfo.values().forEach(v -> v.applyState(state));
    this.state = state;
  }

  @Override
  public boolean isSelected(@NotNull AnActionEvent e) {
    return state;
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    e.getPresentation().setVisible(true);
    super.update(e);
  }
}