import com.intellij.util.Consumer;
import git4idea.GitCommit;
import git4idea.repo.GitRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.api.RefactoringType;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

public class CommitMiner implements Consumer<GitCommit> {

  private Executor pool;
  private Map<String, List<String>> map;
  private Map<String, List<String>> methodsMap;
  private GitRepository repository;
  private MethodRefactoringProcessor processor;

  /**
   * CommitMiner for mining a single commit.
   * @param pool ThreadPool to submit to.
   * @param map Map to add mined commit data to.
   * @param repository GitRepository.
   */
  public CommitMiner(Executor pool, Map<String, List<String>> map,
                     Map<String, List<String>> methodsMap,
                     GitRepository repository) {
    this.pool = pool;
    this.map = map;
    this.methodsMap = methodsMap;
    this.repository = repository;
    this.processor = new MethodRefactoringProcessor(repository.getProject().getBasePath());
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
              null, commitId, new RefactoringHandler() {
                @Override
                public void handle(String commitId, List<Refactoring> refactorings) {
                  System.out.println(commitId);
                  map.put(commitId,
                      refactorings.stream().map(Refactoring::getName).collect(Collectors.toList()));

                  //add methods refactoring types inside the methodsMap
                  List<MethodRefactoringData> refs = refactorings.stream()
                          .map(processor::process).filter(Objects::nonNull)
                          .collect(Collectors.toList());

                  for (MethodRefactoringData ref : refs) {
                    if (!ref.getType().equals(RefactoringType.RENAME_METHOD)) {
                      if (methodsMap.get(ref.getMethodAfter().getName()) == null) {
                        methodsMap.put(ref.getMethodAfter().getName(),
                                Arrays.asList(ref.getType().toString()));
                      } else {
                        List<String> types = methodsMap.get(ref.getMethodAfter().getName());
                        types.add(ref.getType().toString());
                        methodsMap.put(ref.getMethodAfter().getName(), types);
                      }
                    } else {
                      List<String> types =  methodsMap.get(ref.getMethodBefore().getName());
                      methodsMap.remove(ref.getMethodBefore().getName());
                      if (types != null) {
                        types.add(ref.getType().toString());
                        methodsMap.put(ref.getMethodAfter().getName(), types);
                      } else {
                        methodsMap.put(ref.getMethodAfter().getName(),
                                Arrays.asList(ref.getType().toString()));
                      }

                    }
                  }
                }
              });
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    }
  }
}