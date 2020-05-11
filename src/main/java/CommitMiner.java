import com.google.gson.Gson;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.util.Consumer;
import git4idea.GitCommit;
import git4idea.repo.GitRepository;
import gr.uom.java.xmi.diff.MoveAndRenameClassRefactoring;
import gr.uom.java.xmi.diff.MoveClassRefactoring;
import gr.uom.java.xmi.diff.RenameClassRefactoring;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.api.RefactoringType;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

public class CommitMiner implements Consumer<GitCommit> {


  private final Executor pool;
  private final Map<String, List<String>> map;
  private final GitRepository repository;
  private final AtomicInteger commitsDone;
  private final ProgressIndicator progressIndicator;
  private final int limit;
  public List<MethodRefactoring> renameOperations;
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
  public CommitMiner(Executor pool, Map<String, List<String>> map,
                     Map<String, List<String>> methodsMap, GitRepository repository,
                     AtomicInteger commitsDone, ProgressIndicator progressIndicator, int limit) {

    this.pool = pool;
    this.map = map;
    this.methodsMap = methodsMap;
    this.repository = repository;
    this.processor = new MethodRefactoringProcessor(repository.getProject().getBasePath());
    this.renameOperations = new ArrayList<MethodRefactoring>();
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

                  //add methods refactoring types inside the methodsMap
                  List<MethodRefactoring> refs = refactorings.stream()
                      .map(x -> processor.process(x, gitCommit.getCommitTime()))
                      .filter(Objects::nonNull)
                      .map(x -> new MethodRefactoring(x, gitCommit.getId().asString()))
                      .collect(Collectors.toList());
                  addMethodsRefactorings(methodsMap, refs);

                  refactorings
                      .stream()
                      .filter(x -> x.getRefactoringType() == RefactoringType.MOVE_CLASS)
                      .forEach(x -> classRenames
                          .add(new ClassRename(((MoveClassRefactoring) x).getOriginalClassName(),
                              ((MoveClassRefactoring) x).getMovedClassName(),
                              gitCommit.getCommitTime())));

                  refactorings
                      .stream()
                      .filter(x -> x.getRefactoringType() == RefactoringType.MOVE_RENAME_CLASS)
                      .forEach(x -> classRenames.add(new ClassRename(
                          ((MoveAndRenameClassRefactoring) x).getOriginalClassName(),
                          ((MoveAndRenameClassRefactoring) x).getRenamedClassName(),
                          gitCommit.getCommitTime())));

                  refactorings
                      .stream()
                      .filter(x -> x.getRefactoringType() == RefactoringType.RENAME_CLASS)
                      .forEach(x -> classRenames
                          .add(new ClassRename(((RenameClassRefactoring) x).getOriginalClassName(),
                              ((RenameClassRefactoring) x).getRenamedClassName(),
                              gitCommit.getCommitTime())));

                  map.put(commitId, refactorings.stream().map(Refactoring::getName).collect(
                      Collectors.toList()));
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

  /**
   * Helper method for storing the refactorings for methods using the refactoring
   * processor.
   *
   * @param methodsMap the service that stores the methods refactoring history.
   * @param refs       the refactorings to be stored.
   */
  private void addMethodsRefactorings(Map<String, List<String>> methodsMap,
                                      List<MethodRefactoring> refs) {
    for (MethodRefactoring ref : refs) {
      if (!ref.getData().getType().equals(RefactoringType.RENAME_METHOD)
          && !ref.getData().getType().equals(RefactoringType.MOVE_AND_RENAME_OPERATION)) {
        List<String> refBefore = new ArrayList<>();
        refBefore
            .addAll(methodsMap.getOrDefault(ref.getData().getMethodAfter(), new ArrayList<>()));
        refBefore.add(new Gson().toJson(ref));
        methodsMap.put(ref.getData().getMethodAfter(), refBefore);
      } else {
        renameOperations.add(ref);
      }
    }
  }

  private void incrementProgress() {
    final int nCommits = commitsDone.incrementAndGet();
    progressIndicator.setText("Mining refactoring: " + nCommits + "/" + limit);
    progressIndicator.setFraction(
        (float) nCommits / limit);

  }
}