package processors;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.util.Consumer;
import com.intellij.vcs.log.VcsCommitMetadata;
import data.RefactoringEntry;
import git4idea.GitCommit;
import git4idea.repo.GitRepository;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;
import services.RefactoringsBundle;

public class CommitMiner implements Consumer<GitCommit> {


  private static final String progress = RefactoringsBundle.message("progress");
  private final Executor pool;
  private final Map<String, String> map;
  private final GitRepository repository;
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
  public CommitMiner(Executor pool, Map<String, String> map, GitRepository repository,
                     AtomicInteger commitsDone, ProgressIndicator progressIndicator, int limit) {

    this.pool = pool;
    this.map = map;
    this.repository = repository;
    this.commitsDone = commitsDone;
    this.progressIndicator = progressIndicator;
    this.limit = limit;

  }

  /**
   * Method that mines only one commit.
   *
   * @param commit  commit metadata
   * @param map     the inner map that should be updated
   * @param project the current project
   */
  public static void mineAtCommit(VcsCommitMetadata commit, Map<String, String> map,
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

  @Override
  public void consume(GitCommit gitCommit) {
    String commitId = gitCommit.getId().asString();
    if (!map.containsKey(commitId)) {
      pool.execute(() -> {
        GitService gitService = new GitServiceImpl();
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
        try {
          miner.detectAtCommit(gitService.openRepository(repository.getProject().getBasePath()),
              commitId, new RefactoringHandler() {
                @Override
                public void handle(String commitId, List<Refactoring> refactorings) {
                  map.put(commitId,
                      RefactoringEntry
                          .convert(refactorings, gitCommit, repository.getProject()));
                  incrementProgress();
                }
              });
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    } else {
      incrementProgress();
    }
  }

  private void incrementProgress() {
    final int nCommits = commitsDone.incrementAndGet();
    progressIndicator.setText(String.format(progress,
        nCommits, limit));
    progressIndicator.setFraction(
        (float) nCommits / limit);


  }
}