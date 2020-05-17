package processors;

import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import org.jetbrains.annotations.NotNull;
import services.MiningService;

public class CommitListener implements GitRepositoryChangeListener {


  @Override
  public void repositoryChanged(@NotNull GitRepository repository) {
    System.out.println("COMMIT");
    repository.getProject().getService(MiningService.class).mineRepo(repository);
  }
}
