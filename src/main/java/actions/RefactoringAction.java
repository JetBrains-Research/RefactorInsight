package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;
import services.MiningService;

public class RefactoringAction extends AnAction {

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    GitRepository repository =
        GitRepositoryManager.getInstance(e.getProject()).getRepositories().get(0);

    MiningService.getInstance(e.getProject()).mineRepo(repository);
  }

}