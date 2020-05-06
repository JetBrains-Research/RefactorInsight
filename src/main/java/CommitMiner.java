import com.intellij.util.Consumer;
import git4idea.GitCommit;
import git4idea.repo.GitRepository;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class CommitMiner implements Consumer<GitCommit> {

  private Executor pool;
  private Map<String, List<String>> map;
  private GitRepository repository;
  private AtomicInteger commitsDone;

    /**
     * CommitMiner for mining a single commit.
     * @param pool ThreadPool to submit to.
     * @param map Map to add mined commit data to.
     * @param repository GitRepository.
     */
  public CommitMiner(Executor pool, Map<String, List<String>> map, GitRepository repository,
                     AtomicInteger commitsDone) {
    this.pool = pool;
    this.map = map;
    this.repository = repository;
    this.commitsDone = commitsDone;
  }

  @Override
  public void consume(GitCommit gitCommit) {
    System.out.println("joe");
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    com.intellij.diff.DiffContentFactory
    String commitId = gitCommit.getId().asString();
    if (!map.containsKey(commitId)) {
//            checkMemory();
      pool.execute(() -> {
        System.out.println(commitId);
//                GitService gitService = new GitServiceImpl();
//                GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
//                try {
//                    miner.detectAtCommit(gitService.openRepository(repository.getProject().getBasePath()), null, commitId, new RefactoringHandler() {
//                        @Override
//                        public void handle(String commitId, List<Refactoring> refactorings) {
//                            System.out.println(commitId);
////                            map.put(commitId, refactorings.stream().map(Refactoring::getName).collect(Collectors.toList()));
//                            commitsDone.getAndIncrement();
//                        }
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
      });
    }
  }

  private void checkMemory() {
    int queueSize = ((ThreadPoolExecutor) pool).getQueue().size();
    System.out.println(queueSize);
//        if(commitsDone.get() % 100 == 0) System.out.println(commitsDone.get());
    while (queueSize > 20) {
      try {
        Thread.sleep(500);
        queueSize = ((ThreadPoolExecutor) pool).getQueue().size();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}