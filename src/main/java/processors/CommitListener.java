package processors;

import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import org.jetbrains.annotations.NotNull;
import services.MiningService;

/**
 * The CommitListener triggers the MiningService with any new VCS event.
 */
public class CommitListener implements GitRepositoryChangeListener {

  @Override
  public void repositoryChanged(@NotNull GitRepository repository) {
    MiningService.getInstance(repository.getProject()).mineRepo(repository);
  }
}
