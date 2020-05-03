import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import org.jetbrains.annotations.NotNull;

public class CommitListener implements GitRepositoryChangeListener {


    @Override
    public void repositoryChanged(@NotNull GitRepository repository) {
        System.out.println("COMMIT");
    }
}
