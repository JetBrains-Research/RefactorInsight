import com.intellij.openapi.components.*;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsRoot;
import com.intellij.openapi.vcs.impl.ProjectLevelVcsManagerImpl;
import com.intellij.util.Consumer;
import com.intellij.util.xmlb.annotations.MapAnnotation;
import git4idea.GitCommit;
import git4idea.GitReference;
import git4idea.commands.GitCommand;
import git4idea.commands.GitTask;
import git4idea.history.GitHistoryUtils;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@State(name = "ChangesState",
        storages = {@Storage("refactorings.xml")})
@Service
public class MiningService implements PersistentStateComponent<MiningService.MyState> {

    private boolean loaded = false;
    private boolean first = true;
    private boolean mining = false;
    private MyState innerState = new MyState();

    public MiningService(Project project) {
    }

    public static MiningService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, MiningService.class);
    }

    @Override
    public MyState getState() {
        return innerState;
    }

    @Override
    public void loadState(MyState state) {
        innerState = state;
    }

    public void mineRepo(GitRepository repository) {
        if(!loaded) return;
        ProgressManager.getInstance().run(new Task.Backgroundable(repository.getProject(), "Mining refactorings") {
            public void run(@NotNull ProgressIndicator progressIndicator) {
                mining = true;
                progressIndicator.setText("Mining refactorings");
                ExecutorService pool = Executors.newFixedThreadPool(8);
                try {
                    GitHistoryUtils.loadDetails(repository.getProject(), repository.getRoot(),
                            new CommitMiner(pool, innerState.map, repository),
                             "--all");

                } catch (Exception exception) {
                    exception.printStackTrace();
                } finally {
                    mining = false;
                }
                pool.shutdown();
                try {
                    pool.awaitTermination(5, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("done");
                progressIndicator.setText("Finished");
            }
        });
    }

    public List<String> getRefactorings(String commitHash) {
        return innerState.map.getOrDefault(commitHash, Arrays.asList(""));
    }

    public void loaded() {
        loaded = true;
    }

    public static class MyState {
        @NotNull
        @MapAnnotation
        public Map<String, List<String>> map = new ConcurrentHashMap<>();
    }


}
