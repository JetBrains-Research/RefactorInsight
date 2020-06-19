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
import com.intellij.util.xmlb.annotations.OptionTag;
import com.intellij.vcs.log.VcsCommitMetadata;
import data.RefactoringEntry;
import data.RefactoringInfo;
import git4idea.history.GitHistoryUtils;
import git4idea.repo.GitRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;
import processors.CommitMiner;
import ui.windows.GitWindow;
import utils.Utils;

/**
 * This is the MiningService.
 * It computes, process and stores the data retrieved from RefactoringMiner.
 * It can mine 1 specific commit, a fixed number of commits, or all commits in the repository.
 * it stores and persists the detected refactoring data in .idea/refactorings.xml file.
 */
@State(name = "MiningRefactoringsState",
    storages = {@Storage("refactorings.xml")})
@Service
public class MiningService implements PersistentStateComponent<MiningService.MyState> {

  public static ConcurrentHashMap<String, Set<RefactoringInfo>> methodHistory
      = new ConcurrentHashMap<String, Set<RefactoringInfo>>();
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
    if (Utils.version().equals(state.refactoringsMap.version)) {
      innerState = state;
    } else {
      innerState = new MyState();
      innerState.refactoringsMap.version = Utils.version();
    }
  }

  /**
   * Mine complete git repo for refactorings.
   *
   * @param repository GitRepository
   */
  public void mineAll(GitRepository repository) {
    int limit = Integer.MAX_VALUE;
    try {
      limit = Utils.getCommitCount(repository);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      mineRepo(repository, limit);
    }
  }

  /**
   * Mine git repo for refactorings.
   *
   * @param repository GitRepository
   */
  public void mineRepo(GitRepository repository) {
    int limit = SettingsState.getInstance(repository.getProject()).commitLimit;
    try {
      limit = Math.min(Utils.getCommitCount(repository), limit);
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
          @Override
          public void onCancel() {
            super.onCancel();
            return;
          }

          public void run(@NotNull ProgressIndicator progressIndicator) {
            mining = true;
            progressIndicator.setText("Mining refactorings");
            progressIndicator.setIndeterminate(false);
            int cores = SettingsState
                .getInstance(repository.getProject()).threads;
            ExecutorService pool = Executors.newFixedThreadPool(cores);
            System.out.println("Mining started on " + cores + " cores");
            long timeStart = System.currentTimeMillis();
            AtomicInteger commitsDone = new AtomicInteger(0);
            CommitMiner miner =
                new CommitMiner(pool, innerState.refactoringsMap.map, repository, commitsDone,
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

            computeRefactoringHistory(repository.getCurrentRevision(), repository.getProject());
            long timeEnd2 = System.currentTimeMillis();
            double time2 = ((double) (timeEnd2 - timeEnd)) / 1000.0;
            System.out.println("Method history computed in " + time2);
            progressIndicator.setText("Finished");
          }
        });
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

          @Override
          public void onCancel() {
            super.onCancel();
            return;
          }

          public void onFinished() {
            super.onFinished();
            if (contains(commit.getId().asString())) {

              System.out.println("Mining commit done");
              ApplicationManager.getApplication()
                  .invokeLater(() -> info.refresh(commit.getId().asString()));
            } else {
              System.out.println("Mining commit FAILED!");
            }
          }

          public void run(@NotNull ProgressIndicator progressIndicator) {
            CommitMiner.mineAtCommit(commit, innerState.refactoringsMap.map, project);
          }
        });
  }


  public Map<String, Set<RefactoringInfo>> getRefactoringHistory() {
    return methodHistory;
  }

  private void computeRefactoringHistory(@NotNull String commitId, Project project) {
    List<RefactoringInfo> refs = new ArrayList<>();
    int limit = SettingsState.getInstance(project).historyLimit;
    while (contains(commitId) && limit-- > 0) {
      RefactoringEntry refactoringEntry = get(commitId);
      assert refactoringEntry != null;
      refs.addAll(refactoringEntry.getRefactorings());
      commitId = refactoringEntry.getParent();
    }
    Collections.reverse(refs);
    methodHistory.clear();
    refs.forEach(r -> r.addToHistory(methodHistory));
    synchronized (this) {
      notifyAll();
    }
  }

  public RefactoringEntry get(String commitHash) {
    return innerState.refactoringsMap.map.get(commitHash);
  }

  public boolean contains(String commitHash) {
    return innerState.refactoringsMap.map.containsKey(commitHash);
  }

  public void clear() {
    innerState.refactoringsMap.map.clear();
  }

  public static class MyState {
    @OptionTag(converter = RefactoringsMapConverter.class)
    public RefactoringsMap refactoringsMap = new RefactoringsMap();
  }

}
