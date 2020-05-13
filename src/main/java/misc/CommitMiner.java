package misc;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.util.Consumer;
import com.intellij.vcs.log.Hash;
import data.RefactoringEntry;
import data.RefactoringInfo;
import git4idea.GitCommit;
import git4idea.repo.GitRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

public class CommitMiner implements Consumer<GitCommit> {


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
                  System.out.println(commitId);

                  long time = gitCommit.getCommitTime();
                  List<String> parents = gitCommit.getParents()
                      .stream()
                      .map(Hash::asString)
                      .collect(Collectors.toList());
                  map.put(commitId,
                      RefactoringEntry
                          .convert(refactorings, commitId, parents, time));

                  if (parents.size() != 1 && parents.size() != 2) {
                    System.out.println(parents.toString());
                  }

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
    progressIndicator.setText("Mining refactoring: " + nCommits + "/" + limit);
    progressIndicator.setFraction(
        (float) nCommits / limit);

  }
}