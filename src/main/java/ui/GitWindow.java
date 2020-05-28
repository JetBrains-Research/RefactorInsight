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
    if (!gitInfo.containsKey(table)) {
      this.state ^= state;
      gitInfo.put(table, new GitWindowInfo(e));
    } else {
      this.state = state;
    }
    gitInfo.values().forEach(v -> v.applyState(this.state));
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