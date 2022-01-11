package org.jetbrains.research.refactorinsight.services;

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
import com.intellij.vcs.log.VcsFullCommitDetails;
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

import org.eclipse.jgit.lib.Repository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.RefactorInsightBundle;
import org.jetbrains.research.refactorinsight.data.RefactoringEntry;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.processors.CommitMiner;
import org.jetbrains.research.refactorinsight.processors.PRMiningBackgroundableTask;
import org.jetbrains.research.refactorinsight.processors.SingleCommitRefactoringTask;
import org.jetbrains.research.refactorinsight.pullrequests.PRFileEditor;
import org.jetbrains.research.refactorinsight.ui.windows.GitWindow;
import org.jetbrains.research.refactorinsight.utils.Utils;
import org.refactoringminer.util.GitServiceImpl;

/**
 * This is the MiningService.
 * It computes, process and stores the data retrieved from RefactoringMiner.
 * It can mine one specific commit, a fixed number of commits, or all commits in the repository.
 * it stores and persists the detected refactoring data in .idea/refactorings.xml file.
 */
@State(name = "MiningRefactoringsState",
    storages = {@Storage("refactorings.xml")})
@Service
public class MiningService implements PersistentStateComponent<MiningService.MyState> {

  public static ConcurrentHashMap<String, Set<RefactoringInfo>> methodHistory
      = new ConcurrentHashMap<>();
  private boolean mining = false;
  private MyState innerState = new MyState();
  private SingleCommitRefactoringTask task = null;
  private PRMiningBackgroundableTask prTask = null;
  private Repository myRepository = null;

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

  public Repository getRepository() {
    return myRepository;
  }

  public static Repository openRepository(final String path) {
    try {
      return new GitServiceImpl().openRepository(path);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
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
    if (myRepository == null) {
      myRepository = openRepository(repository.getProject().getBasePath());
    }
    ProgressManager.getInstance()
        .run(new Task.Backgroundable(repository.getProject(), RefactorInsightBundle.message("mining"), true) {

          public void run(@NotNull ProgressIndicator progressIndicator) {
            mining = true;
            progressIndicator.setText(RefactorInsightBundle.message("mining"));
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
              GitHistoryUtils.loadTimedCommits(repository.getProject(), repository.getRoot(),
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
            progressIndicator.setText(RefactorInsightBundle.message("finished"));
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
   * Mine refactorings in the specific commit.
   *
   * @param commit  to be mined.
   * @param project current project.
   * @param window  to be updated.
   */
  public void mineAtCommit(VcsCommitMetadata commit, Project project, GitWindow window) {
    if (task != null) {
      task.cancel();
    }
    if (myRepository == null) {
      myRepository = openRepository(project.getBasePath());
    }

    task = new SingleCommitRefactoringTask(project, commit, window);
    ProgressManager.getInstance().run(task);
  }

  /**
   * Runs detection of refactorings in Pull Request.
   *
   * @param commitDetails pull request's commits details.
   * @param project       current project.
   * @param scrollPane    scrollPane to be updated.
   */
  public void mineAtCommitFromPR(List<VcsFullCommitDetails> commitDetails,
                                 Project project, PRFileEditor scrollPane) {
    if (myRepository == null) {
      myRepository = openRepository(project.getBasePath());
    }
    prTask = new PRMiningBackgroundableTask(project, commitDetails, scrollPane);
    ProgressManager.getInstance().run(prTask);
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
