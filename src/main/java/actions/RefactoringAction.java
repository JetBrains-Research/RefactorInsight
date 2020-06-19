package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import services.MiningService;


/**
 * This is the Mine All Refactorings Action.
 * It retrieves the git repository of the current project iff it exists.
 * If the currently opened project is not a git repository,
 * an error message is shown.
 * Calls the MiningService in order to mine all commits.
 */
public class RefactoringAction extends AnAction {

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    final List<GitRepository> repositories = GitRepositoryManager
        .getInstance(e.getProject()).getRepositories();
    if (repositories.isEmpty()) {
      Messages.showErrorDialog("Your project is not connected to VCS.",
          "Refactorings Detection");
      return;
    }
    GitRepository repository = repositories.get(0);
    MiningService.getInstance(e.getProject()).clear();
    MiningService.getInstance(e.getProject()).mineAll(repository);
  }

}