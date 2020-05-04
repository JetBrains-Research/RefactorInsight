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
import java.util.stream.Collectors;

@State(name = "ChangesState",
        storages = {@Storage("refactorings.xml")})
@Service
public class MiningService implements PersistentStateComponent<MiningService.MyState> {

    private boolean loaded = false;
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
                GitService gitService = new GitServiceImpl();
                GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
                try {
                    GitHistoryUtils.loadDetails(repository.getProject(), repository.getRoot(), gitCommit -> {
                        String commitId = gitCommit.getId().asString();
                        if (!innerState.map.containsKey(commitId)) {
                            try {
                                miner.detectAtCommit(gitService.openRepository(repository.getProject().getBasePath()), null, commitId, new RefactoringHandler() {
                                    @Override
                                    public void handle(String commitId, List<Refactoring> refactorings) {
                                        System.out.println(commitId);
                                        innerState.map.put(commitId, refactorings.stream().map(Refactoring::getName).collect(Collectors.toList()));
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, "--all");
                    System.out.println("done");
                } catch (Exception exception) {
                    exception.printStackTrace();
                } finally {
                    mining = false;
                }
                progressIndicator.setText("Finished");
            }
        });
    }

    public List<String> getRefactorings(String commitHash) {
        return innerState.map.getOrDefault(commitHash, Arrays.asList("Commit not mined"));
    }

    public void loaded() {
        loaded = true;
    }

    public static class MyState {
        @NotNull
        @MapAnnotation
        public Map<String, List<String>> map = new HashMap<String, List<String>>();
    }

}
