import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.util.Consumer;
import com.intellij.vcs.log.Hash;
import git4idea.GitCommit;
import git4idea.repo.GitRepository;
import java.util.ArrayList;
import java.util.Arrays;
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

public class CommitMiner implements Consumer<GitCommit> {


  private final Executor pool;
  private final Map<String, String> map;
  private final GitRepository repository;
  private final AtomicInteger commitsDone;
  private final ProgressIndicator progressIndicator;
  private final int limit;
  public List<RefactoringInfo> renameOperations;
  public List<ClassRename> classRenames;
  private Map<String, List<String>> methodsMap;
  private MethodRefactoringProcessor processor;


  /**
   * CommitMiner for mining a single commit.
   *
   * @param pool       ThreadPool to submit to.
   * @param map        Map to add mined commit data to.
   * @param repository GitRepository.
   */
  public CommitMiner(Executor pool, Map<String, String> map,
                     Map<String, List<String>> methodsMap, GitRepository repository,
                     AtomicInteger commitsDone, ProgressIndicator progressIndicator, int limit) {

    this.pool = pool;
    this.map = map;
    this.methodsMap = methodsMap;
    this.repository = repository;
    this.processor = new MethodRefactoringProcessor(repository.getProject().getBasePath());
    this.renameOperations = new ArrayList<RefactoringInfo>();
    this.classRenames = new ArrayList<>();
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

                  //processMethodHistory(refactorings, commitId);
                  List<Hash> parents = gitCommit.getParents();
                  if (parents.size() > 0) {
                    map.put(commitId,
                        RefactoringEntry
                            .convert(refactorings, commitId, parents.get(0).asString()));
                  }
                  if (parents.size() != 1) {
                    System.out.println(Arrays.toString(parents.toArray()));
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

  /*private void processMethodHistory(List<Refactoring> refactorings, String hash) {

    //add methods refactoring types inside the methodsMap
    List<RefactoringEntry> refs = refactorings.stream()
        .map(x -> processor.process(x))
        .filter(Objects::nonNull)
        .map(x -> new RefactoringEntry(x, hash))
        .collect(Collectors.toList());
    addMethodsRefactorings(methodsMap, refs);

    refactorings
        .stream()
        .filter(x -> x.getRefactoringType() == RefactoringType.MOVE_CLASS)
        .forEach(x -> classRenames
            .add(new ClassRename(((MoveClassRefactoring) x).getOriginalClassName(),
                ((MoveClassRefactoring) x).getMovedClassName())));

    refactorings
        .stream()
        .filter(x -> x.getRefactoringType() == RefactoringType.MOVE_RENAME_CLASS)
        .forEach(x -> classRenames.add(new ClassRename(
            ((MoveAndRenameClassRefactoring) x).getOriginalClassName(),
            ((MoveAndRenameClassRefactoring) x).getRenamedClassName())));

    refactorings
        .stream()
        .filter(x -> x.getRefactoringType() == RefactoringType.RENAME_CLASS)
        .forEach(x -> classRenames
            .add(new ClassRename(((RenameClassRefactoring) x).getOriginalClassName(),
                ((RenameClassRefactoring) x).getRenamedClassName())));
  }*/


  private void incrementProgress() {
    final int nCommits = commitsDone.incrementAndGet();
    progressIndicator.setText("Mining refactoring: " + nCommits + "/" + limit);
    progressIndicator.setFraction(
        (float) nCommits / limit);

  }
}