package org.jetbrains.research.refactorinsight.services;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.vcs.log.ui.MainVcsLogUi;
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
  private final Project project;
  private Map<VcsLogGraphTable, GitWindow> gitInfo = new HashMap<>();

  public WindowService(@NotNull Project p) {
    project = p;
  }

  public static WindowService getInstance(@NotNull Project project) {
    return ServiceManager.getService(project, WindowService.class);
  }

  /**
   * Applies selects or deselects the tool window.
   *
   * @param ui    target vcs log tab
   * @param state true for selected, false for unselected
   */
  public void setSelected(@NotNull MainVcsLogUi ui, boolean state) {
    GitWindow gitWindow = gitInfo.get(ui.getTable());
    gitWindow.setSelected(state);
  }

  public boolean isSelected(@NotNull MainVcsLogUi vcsLogUi) {
    VcsLogGraphTable table = vcsLogUi.getTable();
    return gitInfo.containsKey(table) && gitInfo.get(table).isSelected();
  }

  /**
   * Sets visibility of refactoring labels in VCSTable.
   * @param e Event
   * @param visible boolean
   */
  public void setLabelsVisible(@NotNull AnActionEvent e, boolean visible) {
    if (e.getData(VcsLogInternalDataKeys.MAIN_UI) == null) {
      return;
    }
    VcsLogGraphTable table = e.getData(VcsLogInternalDataKeys.MAIN_UI).getTable();
    gitInfo.get(table).setLabelsVisible(visible);
  }

  /**
   * Checks if the displayed table has the lables visible.
   * @param e the action event
   * @return true if the labels are visible, false otherwise
   */
  public boolean isLabelsVisible(@NotNull AnActionEvent e) {
    if (e.getData(VcsLogInternalDataKeys.MAIN_UI) == null) {
      return false;
    }
    VcsLogGraphTable table = e.getData(VcsLogInternalDataKeys.MAIN_UI).getTable();
    return gitInfo.containsKey(table) && gitInfo.get(table).isLabelsVisible();
  }

  /**
   * Generates if needed a GitWindow (RefactorInsight) object.
   * @param e Event
   */
  public void update(@NotNull AnActionEvent e) {
    if (e.getData(VcsLogInternalDataKeys.MAIN_UI) == null) {
      return;
    }
    MainVcsLogUi ui = e.getData(VcsLogInternalDataKeys.MAIN_UI);
    gitInfo.computeIfAbsent(ui.getTable(), table -> {
      Disposer.register(ui, () -> gitInfo.remove(ui.getTable()));
      return new GitWindow(project, ui);
    });
  }
}
