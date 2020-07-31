package org.jetbrains.research.refactorinsight.services;

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
import org.jetbrains.research.refactorinsight.data.RefactoringEntry;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.processors.CommitMiner;
import org.jetbrains.research.refactorinsight.ui.windows.GitWindow;
import org.jetbrains.research.refactorinsight.utils.Utils;

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
        .run(new Task.Backgroundable(repository.getProject(), "Mining refactorings", true) {

          @Override
          public void onCancel() {
            super.onCancel();
          }

          public void run(@NotNull ProgressIndicator progressIndicator) {
            mining = true;
            progressIndicator.setText(RefactoringsBundle.message("mining"));
            progressIndicator.setIndeterminate(false);
            int cores = SettingsState
                .getInstance(repository.getProject()).threads;
            ExecutorService pool = Executors.newFixedThreadPool(cores);
            AtomicInteger commitsDone = new AtomicInteger(0);
            CommitMiner miner =
                new CommitMiner(pool, innerState.refactoringsMap.map, repository, commitsDone,
                    progressIndicator,
                    limit);
            progressIndicator.checkCanceled();
            try {
              String logArgs = "--max-count=" + limit;
              progressIndicator.checkCanceled();
              GitHistoryUtils.loadDetails(repository.getProject(), repository.getRoot(),
                  miner, logArgs);
              progressIndicator.checkCanceled();
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
            if (repository.getCurrentRevision() != null) {
              computeRefactoringHistory(repository.getCurrentRevision(), repository.getProject());
            }
            progressIndicator.setText(RefactoringsBundle.message("finished"));
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
    ProgressManager.getInstance()
        .run(new Task.Backgroundable(project, String.format(
            RefactoringsBundle.message("mining.at"), commit.getId().asString())) {

          @Override
          public void onCancel() {
            super.onCancel();
          }

          public void onFinished() {
            super.onFinished();
            if (containsCommit(commit.getId().asString())) {
              System.out.println(RefactoringsBundle.message("finished"));
              ApplicationManager.getApplication()
                  .invokeLater(() -> info.refresh(commit.getId().asString()));
            }
          }

          public void run(@NotNull ProgressIndicator progressIndicator) {
              CommitMiner.mineAtCommitTimeout(commit, innerState.refactoringsMap.map, project);
          }
        });
  }


  public Map<String, Set<RefactoringInfo>> getRefactoringHistory() {
    return methodHistory;
  }

  private void computeRefactoringHistory(@NotNull String commitId, Project project) {
    List<RefactoringInfo> refs = new ArrayList<>();
    final SettingsState settingsState = SettingsState.getInstance(project);
    int limit = settingsState != null ? settingsState.historyLimit : Integer.MAX_VALUE / 100;
    while (containsCommit(commitId) && limit-- > 0) {
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

  public boolean containsCommit(String commitHash) {
    return innerState.refactoringsMap.map.containsKey(commitHash);
  }

  public boolean containsRefactoring(String commitHash) {
    return innerState.refactoringsMap.map.containsKey(commitHash)
        && innerState.refactoringsMap.map.get(commitHash).getRefactorings().size() != 0;
  }

  public void clear() {
    innerState.refactoringsMap.map.clear();
  }

  public static class MyState {
    @OptionTag(converter = RefactoringsMapConverter.class)
    public RefactoringsMap refactoringsMap = new RefactoringsMap();
  }

}
