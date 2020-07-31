package org.jetbrains.research.refactorinsight.processors;

import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.util.Consumer;
import com.intellij.vcs.log.VcsCommitMetadata;
import git4idea.GitCommit;
import git4idea.repo.GitRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.jgit.lib.Repository;
import org.jetbrains.research.refactorinsight.data.RefactoringEntry;
import org.jetbrains.research.refactorinsight.RefactorInsightBundle;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

/**
 * The CommitMiner is a Consumer of GitCommit.
 * It mines a commit and updates the refactoring map with the data retrieved for that commit.
 * Consumes a git commit, calls RefactoringMiner and detects the refactorings for a commit.
 */
public class CommitMiner implements Consumer<GitCommit> {
  private static final String progress = RefactorInsightBundle.message("progress");
  private final ExecutorService pool;
  private final Map<String, RefactoringEntry> map;
  private final Project myProject;
  private final Repository myRepository;
  private final AtomicInteger commitsDone;
  private final ProgressIndicator progressIndicator;
  private final int limit;

  /**
   * misc.CommitMiner for mining a single commit.
   *
   * @param pool       ThreadPool to submit to.
   * @param map        Map to add mined commit data to.
   * @param repository GitRepository.
   */
  public CommitMiner(ExecutorService pool, Map<String, RefactoringEntry> map,
                     GitRepository repository,
                     AtomicInteger commitsDone, ProgressIndicator progressIndicator, int limit) {

    this.pool = pool;
    this.map = map;
    myProject = repository.getProject();
    //NB: nullable, check if initialized correctly
    myRepository = openRepository(myProject.getBasePath());
    this.commitsDone = commitsDone;
    this.progressIndicator = progressIndicator;
    this.limit = limit;
  }

  private static Repository openRepository(final String path) {
    try {
      return new GitServiceImpl().openRepository(path);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Method that mines only one commit.
   *
   * @param commit  commit metadata
   * @param map     the inner map that should be updated
   * @param project the current project
   */
  public static void mineAtCommit(VcsCommitMetadata commit, Map<String, RefactoringEntry> map,
                                  Project project) {
    GitService gitService = new GitServiceImpl();
    GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
    try {
      miner.detectAtCommit(gitService.openRepository(project.getBasePath()),
          commit.getId().asString(),
          new RefactoringHandler() {
            @Override
            public void handle(String commitId, List<Refactoring> refactorings) {
              map.put(commitId, RefactoringEntry.convert(refactorings, commit, project));
            }
          }
      );
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Mines a single commit if the mining process does not take longer than
   * 60 seconds.
   *
   * @param commit  to be mined
   * @param map     refactorings map
   * @param project the current project
   */
  public static void mineAtCommitTimeout(VcsCommitMetadata commit,
                                         Map<String, RefactoringEntry> map,
                                         Project project) {
    GitService gitService = new GitServiceImpl();
    GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
    ExecutorService service = Executors.newSingleThreadExecutor();
    Future<?> f = null;
    final Repository repository;
    try {
      repository = gitService.openRepository(project.getBasePath());
    } catch (Exception e) {
      return;
    }
    try {
      Runnable r = () -> {
        miner.detectAtCommit(repository,
            commit.getId().asString(), new RefactoringHandler() {
              @Override
              public void handle(String commitId, List<Refactoring> refactorings) {
                map.put(commitId,
                    RefactoringEntry
                        .convert(refactorings, commit, project));
              }
            });
      };
      f = service.submit(r);
      f.get(60, TimeUnit.SECONDS);
    } catch (TimeoutException e) {
      if (f.cancel(true)) {
        RefactoringEntry refactoringEntry = RefactoringEntry
            .convert(new ArrayList<>(), commit, project);
        refactoringEntry.setTimeout(true);
        map.put(commit.getId().asString(), refactoringEntry);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      service.shutdown();
    }
  }

  /**
   * Mines a gitCommit.
   * Method that calls RefactoringMiner and updates the refactoring map.
   *
   * @param gitCommit to be mined
   */
  public void consume(GitCommit gitCommit) throws ProcessCanceledException {
    String commitId = gitCommit.getId().asString();

    if (!map.containsKey(commitId)) {
      pool.execute(() -> {
        if (progressIndicator.isCanceled()) {
          cancelProgress();
          return;
        }
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<?> f = null;
        try {
          Runnable r = () -> miner.detectAtCommit(myRepository, commitId, new RefactoringHandler() {
            @Override
            public void handle(String commitId, List<Refactoring> refactorings) {
              map.put(commitId,
                  RefactoringEntry
                      .convert(refactorings, gitCommit, myProject));
              incrementProgress();
            }
          });
          f = service.submit(r);
          f.get(60, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
          if (f.cancel(true)) {
            RefactoringEntry refactoringEntry = RefactoringEntry
                .convert(new ArrayList<>(), gitCommit, myProject);
            refactoringEntry.setTimeout(true);
            map.put(commitId, refactoringEntry);
          }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          service.shutdown();
        }
      });
    } else {
      incrementProgress();
      progressIndicator.checkCanceled();
    }
  }

  /**
   * Increments the progress bar with each mined commit.
   */
  private void incrementProgress() {
    final int nCommits = commitsDone.incrementAndGet();
    progressIndicator.setText(String.format(progress,
        nCommits, limit));
    progressIndicator.setFraction((float) nCommits / limit);
  }

  private void cancelProgress() {
    final int nCommits = commitsDone.incrementAndGet();
    progressIndicator.setFraction((float) nCommits / limit);
    progressIndicator.setText("Cancelling");
  }
}