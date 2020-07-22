package org.jetbrains.research.refactorinsight.services;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.vcs.log.ui.MainVcsLogUi;
import com.intellij.vcs.log.ui.table.VcsLogGraphTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.ui.windows.GitWindow;

import java.util.HashMap;
import java.util.Map;

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
    GitWindow gitWindow = gitInfo.computeIfAbsent(ui.getTable(), table -> {
      Disposer.register(ui, () -> gitInfo.remove(ui.getTable()));
      return new GitWindow(project, ui);
    });
    gitWindow.setSelected(state);
  }

  public boolean isSelected(@NotNull MainVcsLogUi vcsLogUi) {
    VcsLogGraphTable table = vcsLogUi.getTable();
    return gitInfo.containsKey(table) && gitInfo.get(table).isSelected();
  }
}
