package org.jetbrains.research.refactorinsight.services;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.vcs.log.ui.VcsLogInternalDataKeys;
import com.intellij.vcs.log.ui.table.VcsLogGraphTable;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.ui.windows.GitWindow;

/**
 * Service that holds a git window map per opened project.
 * Provides useful information for all the refactoring tree git windows that are active.
 */
@Service
public class WindowService {

  private Map<VcsLogGraphTable, GitWindow> gitInfo = new HashMap<>();

  public WindowService() {
  }

  public static WindowService getInstance(@NotNull Project project) {
    return ServiceManager.getService(project, WindowService.class);
  }

  /**
   * Applies selects or deselects the tool window.
   *
   * @param e     the action event
   * @param state true for selected, false for unselected
   */
  public void setSelected(@NotNull AnActionEvent e, boolean state) {
    VcsLogGraphTable table = e.getData(VcsLogInternalDataKeys.MAIN_UI).getTable();
    gitInfo.putIfAbsent(table, new GitWindow(e));
    gitInfo.get(table).setSelected(state);
  }

  public boolean isSelected(@NotNull AnActionEvent e) {
    VcsLogGraphTable table = e.getData(VcsLogInternalDataKeys.MAIN_UI).getTable();
    return gitInfo.containsKey(table) && gitInfo.get(table).isSelected();
  }
}
