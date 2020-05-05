import com.intellij.openapi.progress.ProgressManager;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import org.jetbrains.annotations.NotNull;

public class CommitListener implements GitRepositoryChangeListener {


    @Override
    public void repositoryChanged(@NotNull GitRepository repository) {
        System.out.println("COMMIT");
        repository.getProject().getService(MiningService.class).mineRepo(repository);
    }
}
