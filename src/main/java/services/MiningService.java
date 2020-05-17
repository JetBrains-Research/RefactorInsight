package services;

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
import data.RefactoringEntry;
import data.RefactoringInfo;
import git4idea.history.GitHistoryUtils;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;
import processors.CommitMiner;

@State(name = "ChangesState",
    storages = {@Storage("refactorings.xml")})
@Service
public class MiningService implements PersistentStateComponent<MiningService.MyState> {

  public static ConcurrentHashMap<String, List<RefactoringInfo>> methodHistory
      = new ConcurrentHashMap<>();
  private boolean loaded = false;
  private boolean first = true;
  private boolean mining = false;
  private Project project;
  private MyState innerState = new MyState();

  public MiningService(Project project) {
    this.project = project;
  }

  public static MiningService getInstance(@NotNull Project project) {
    return ServiceManager.getService(project, MiningService.class);
  }

  public boolean isMining() {
    return mining;
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
            CommitMiner miner =
                new CommitMiner(pool, innerState.map, repository, commitsDone,
                    progressIndicator,
                    limit);
            try {
              String logArgs = "--max-count=" + limit;
              GitHistoryUtils.loadDetails(repository.getProject(), repository.getRoot(),
                  miner, logArgs);
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
            getOrderedRefactorings(repository.getCurrentRevision())
                .forEach(r -> r.addToHistory(methodHistory));

            System.out.println("done");
            progressIndicator.setText("Finished");
          }
        });
  }

  public String getRefactorings(String commitHash) {
    return innerState.map.getOrDefault(commitHash, "");
  }

  /**
   * Sets loaded = true, which allows refactoring miner to run.
   */
  public void loaded() {
    if (loaded) {
      return;
    }
    loaded = true;
    //mineRepo(GitRepositoryManager.getInstance(project).getRepositories().get(0));
  }

  /**
   * Get the total ammount of commits in a repository.
   *
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

  public Map<String, List<RefactoringInfo>> getMethodHistory() {
    return methodHistory;
  }

  private Collection<RefactoringInfo> getOrderedRefactorings(String commitId) {
    List<RefactoringInfo> refs = new ArrayList<>();
    PriorityQueue<RefactoringEntry> queue = new PriorityQueue<>(
        Comparator.comparingLong(RefactoringEntry::getTimeStamp).reversed());
    if(innerState.map.containsKey(commitId)) {
      queue.add(RefactoringEntry.fromString(innerState.map.get(commitId)));
    }
    Set<String> visited = new HashSet<>();
    while (!queue.isEmpty()) {
      RefactoringEntry refactoringEntry = queue.remove();
      refs.addAll(refactoringEntry.getRefactorings());
      visited.add(refactoringEntry.getCommitId());
      refactoringEntry.getParents().forEach(e -> {
        if (innerState.map.containsKey(e) && !visited.contains(e)) {
          queue.add(RefactoringEntry.fromString(innerState.map.get(e)));
          visited.add(e);
        }
      });
    }
    Collections.reverse(refs);
    return refs;
  }

  public static class MyState {
    @NotNull
    @MapAnnotation
    public Map<String, String> map = new ConcurrentHashMap<>();
  }
}
