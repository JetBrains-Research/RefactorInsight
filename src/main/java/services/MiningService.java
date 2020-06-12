package services;

import com.intellij.openapi.application.ApplicationManager;
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
import com.intellij.vcs.log.VcsCommitMetadata;
import data.RefactoringEntry;
import data.RefactoringInfo;
import data.RefactoringLine;
import git4idea.history.GitHistoryUtils;
import git4idea.repo.GitRepository;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import processors.CommitMiner;
import ui.windows.GitWindow;

@State(name = "MiningRefactoringsState",
    storages = {@Storage("refactorings.xml")})
@Service
public class MiningService implements PersistentStateComponent<MiningService.MyState> {

  private static final String VERSION = "1.0.1";
  public static ConcurrentHashMap<String, List<RefactoringInfo>> methodHistory
      = new ConcurrentHashMap<>();
  private boolean mining = false;
  private MyState innerState = new MyState();

  public MiningService() {
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
    if (version().equals(state.map.get("version"))) {
      innerState = state;
    } else {
      innerState = new MyState();
      innerState.map.put("version", version());
    }
  }

  /**
   * Mine complete git repo for refactorings.
   *
   * @param repository GitRepository
   */
  public void mineRepo(GitRepository repository) {
    int limit = 100;
    try {
      limit = getCommitCount(repository);
    } catch (Exception e) {
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
    ProgressManager.getInstance()
        .run(new Task.Backgroundable(repository.getProject(), "Mining refactorings") {
          public void run(@NotNull ProgressIndicator progressIndicator) {
            mining = true;
            progressIndicator.setText("Mining refactorings");
            progressIndicator.setIndeterminate(false);
            int cores = 8; //Runtime.getRuntime().availableProcessors();
            ExecutorService pool = Executors.newFixedThreadPool(cores);
            System.out.println("Mining started on " + cores + " cores");
            long timeStart = System.currentTimeMillis();
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
            long timeEnd = System.currentTimeMillis();
            double time = ((double) (timeEnd - timeStart)) / 1000.0;
            System.out.println("Mining done in " + time + " sec");

            computeMethodHistory(repository.getCurrentRevision());
            System.out.println("Method history computed");

            progressIndicator.setText("Finished");
          }
        });
    synchronized (this) {
      notifyAll();
    }
  }

  /**
   * Mine complete git repo for refactorings, and wait to be done.
   *
   * @param repository GitRepository
   */
  public void mineAndWait(GitRepository repository) {
    synchronized (this) {
      mineRepo(repository);
      while (mining) {
        try {
          wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Method for mining a single commit.
   *
   * @param commit  to be mined.
   * @param project current project.
   * @param info    to be updated.
   */
  public void mineAtCommit(VcsCommitMetadata commit, Project project, GitWindow info) {
    System.out.println("Mining commit " + commit.getId().asString());
    ProgressManager.getInstance()
        .run(new Task.Backgroundable(project, "Mining at commit " + commit.getId().asString()) {

          public void onFinished() {
            super.onFinished();
            if (innerState.map.containsKey(commit.getId().asString())) {
              System.out.println("Mining commit done");
              ApplicationManager.getApplication()
                  .invokeLater(() -> info.refresh(commit.getId().asString()));
            } else {
              System.out.println("Mining commit FAILED!");
            }
          }

          public void run(@NotNull ProgressIndicator progressIndicator) {
            CommitMiner.mineAtCommit(commit, innerState.map, project);
          }
        });
  }

  public String getRefactorings(String commitHash) {
    return innerState.map.getOrDefault(commitHash, "");
  }

  /**
   * Get the total amount of commits in a repository.
   *
   * @param repository GitRepository
   * @return the amount of commits
   * @throws IOException in case of a problem
   */
  public int getCommitCount(GitRepository repository) throws IOException {
    Process process = Runtime.getRuntime().exec("git rev-list --all --count", null,
        new File(repository.getRoot().getCanonicalPath()));
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    String output = reader.readLine();
    return Integer.parseInt(output);
  }

  public Map<String, List<RefactoringInfo>> getMethodHistory() {
    return methodHistory;
  }

  private void computeMethodHistory(@NotNull String commitId) {
    List<RefactoringInfo> refs = new ArrayList<>();
    while (innerState.map.containsKey(commitId)) {
      RefactoringEntry refactoringEntry = RefactoringEntry.fromString(innerState.map.get(commitId));
      assert refactoringEntry != null;
      refs.addAll(refactoringEntry.getRefactorings());
      commitId = refactoringEntry.getParent();
    }
    Collections.reverse(refs);
    refs.forEach(r -> r.addToHistory(methodHistory));
  }

  public RefactoringEntry getEntry(String hash) {
    return RefactoringEntry.fromString(innerState.map.get(hash));
  }

  private String version() {
    return RefactoringsBundle.message("version") + String.valueOf(Stream.of(
        //all classes that can change
        RefactoringEntry.class,
        RefactoringInfo.class,
        RefactoringLine.class,
        RefactoringLine.RefactoringOffset.class
    ).flatMap(c -> Arrays.stream(c.getDeclaredFields())
        .map(Field::getGenericType)
        .map(Type::getTypeName)
    ).collect(Collectors.toList()))
        .hashCode();
  }

  public static class MyState {
    @NotNull
    @MapAnnotation
    public Map<String, String> map = new ConcurrentHashMap<>();
  }

}
