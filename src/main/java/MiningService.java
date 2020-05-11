import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.annotations.MapAnnotation;
import git4idea.history.GitHistoryUtils;
import git4idea.repo.GitRepository;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;

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

  /**
   * Mine compelete git repo for refactorings.
   *
   * @param repository GitRepository
   */
  public void mineRepo(GitRepository repository) {
    if (!loaded) {
      return;
    }

    int limit = 100;
    try {
      limit = getCommitCount(repository);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      mineRepo(repository, limit);
    }

  }

  /**
   * Mine repo with limit.
   *
   * @param repository GitRepository
   * @param limit      int
   */
  public void mineRepo(GitRepository repository, int limit) {
    if (!loaded) {
      return;
    }
    ProgressManager.getInstance()
        .run(new Task.Backgroundable(repository.getProject(), "Mining refactorings") {
          public void run(@NotNull ProgressIndicator progressIndicator) {
            mining = true;
            progressIndicator.setText("Mining refactorings");
            progressIndicator.setIndeterminate(false);
            int cores = 8; //Runtime.getRuntime().availableProcessors();
            ExecutorService pool = Executors.newFixedThreadPool(cores);
            System.out.println("Mining started on " + cores + " cores");
            AtomicInteger commitsDone = new AtomicInteger(0);
            try {
              String logArgs = "--max-count=" + limit;
              GitHistoryUtils.loadDetails(repository.getProject(), repository.getRoot(),
                  new CommitMiner(pool, innerState.map, repository, commitsDone, progressIndicator,
                      limit),
                  logArgs);

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

  /**
   * Get the total ammount of commits in a repository.
   * @param repository GitRepository
   * @return int, ammount of commits
   * @throws IOException bla
   */
  public int getCommitCount(GitRepository repository) throws IOException {
    Process process = Runtime.getRuntime().exec("git rev-list --all --count", null,
        new File(repository.getRoot().getCanonicalPath()));
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    String output = reader.readLine();
    System.out.println(output);
    return Integer.parseInt(output);
  }

  public static class MyState {
    @NotNull
    @MapAnnotation
    public Map<String, List<String>> map = new ConcurrentHashMap<>();
  }

}
