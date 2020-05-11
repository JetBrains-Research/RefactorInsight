import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;

public class RefactoringAction extends AnAction {

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    GitRepository repository =
        GitRepositoryManager.getInstance(e.getProject()).getRepositories().get(0);
    e.getProject().getService(MiningService.class)
        .mineRepo(repository, e.getProject().getService(MethodService.class).getState().map);
  }
}