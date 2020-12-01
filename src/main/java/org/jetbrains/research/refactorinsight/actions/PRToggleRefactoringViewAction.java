package org.jetbrains.research.refactorinsight.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.DumbAwareToggleAction;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.github.api.data.GHCommit;
import org.jetbrains.plugins.github.pullrequest.action.GHPRActionKeys;
import org.jetbrains.plugins.github.pullrequest.data.provider.GHPRChangesDataProvider;
import org.jetbrains.research.refactorinsight.services.WindowService;
import org.jetbrains.research.refactorinsight.pullrequests.PRVirtualFile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * An action that toggles the detection of refactorings in opened Pull Request.
 */
public class PRToggleRefactoringViewAction extends DumbAwareToggleAction {

  @Override
  public void setSelected(@NotNull AnActionEvent e, boolean state) {
    Project project = e.getRequiredData(PlatformDataKeys.PROJECT);
    @NotNull GHPRChangesDataProvider ghprChangesDataProvider =
        e.getRequiredData(GHPRActionKeys.getPULL_REQUEST_DATA_PROVIDER()).getChangesData();
    @NotNull CompletableFuture<List<GHCommit>> loadedCommits = ghprChangesDataProvider.loadCommitsFromApi();
    List<String> commitIds = new ArrayList<>();
    try {
      List<GHCommit> ghCommits = loadedCommits.get();
      for (GHCommit ghCommit : ghCommits) {
        ghCommit.getParents().get(0).getId();
        commitIds.add(ghCommit.getOid());
      }
    } catch (InterruptedException | ExecutionException interruptedException) {
      interruptedException.printStackTrace();
    }
    PRVirtualFile prVirtualFile = new PRVirtualFile("Discovered refactorings in PR", null, 0, commitIds);
    ApplicationManager.getApplication()
        .invokeAndWait(() -> FileEditorManager.getInstance(project)
            .openEditor(new OpenFileDescriptor(project, prVirtualFile), true));
  }

  @Override
  public boolean isSelected(@NotNull AnActionEvent e) {
    return false;
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    e.getPresentation().setEnabledAndVisible(isEnabled(e));
    WindowService.getInstance(e.getProject()).update(e);
    super.update(e);
  }

  private boolean isEnabled(@NotNull AnActionEvent e) {
    return e.getProject() != null;
  }
}
