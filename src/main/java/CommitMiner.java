import com.intellij.util.Consumer;
import git4idea.GitCommit;
import git4idea.repo.GitRepository;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class CommitMiner implements Consumer<GitCommit> {

    private Executor pool;
    private Map<String, List<String>> map;
    private GitRepository repository;

    public CommitMiner(Executor pool, Map<String, List<String>> map, GitRepository repository) {
        this.pool = pool;
        this.map = map;
        this.repository = repository;
    }

    @Override
    public void consume(GitCommit gitCommit) {
        String commitId = gitCommit.getId().asString();
        if (!map.containsKey(commitId)) {
            pool.execute(() -> {
                GitService gitService = new GitServiceImpl();
                GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
                try {
                    miner.detectAtCommit(gitService.openRepository(repository.getProject().getBasePath()), null, commitId, new RefactoringHandler() {
                        @Override
                        public void handle(String commitId, List<Refactoring> refactorings) {
                            System.out.println(commitId);
                            map.put(commitId, refactorings.stream().map(Refactoring::getName).collect(Collectors.toList()));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}