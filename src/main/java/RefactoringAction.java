import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsRoot;
import com.intellij.openapi.vcs.impl.ProjectLevelVcsManagerImpl;
import git4idea.GitReference;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.util.*;

public class RefactoringAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project currentProject = e.getProject();
        MiningService miningService = currentProject.getService(MiningService.class);
        Map map = miningService.getState().map;
        map.clear();
        final ProjectLevelVcsManagerImpl instance = (ProjectLevelVcsManagerImpl) ProjectLevelVcsManager.getInstance(currentProject);
        final VcsRoot gitRootPath = Arrays.stream(instance.getAllVcsRoots()).filter(x -> x.getVcs() != null)
                .filter(x -> x.getVcs().getName().equalsIgnoreCase("git"))
                .findAny().orElse(null);

        if (gitRootPath == null) System.out.println("no repo");

        String branch = GitRepositoryManager.getInstance(currentProject).getRepositories().stream().filter(x -> x.getRoot().equals(gitRootPath.getPath()))
                .map(GitRepository::getCurrentBranch)
                .filter(Objects::nonNull)
                .map(GitReference::getName)
                .findFirst().orElse("master");


        GitService gitService = new GitServiceImpl();
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

        try {
            miner.detectAll(gitService.openRepository(currentProject.getBasePath()), branch, new RefactoringHandler() {
                @Override
                public void handle(String commitId, List<Refactoring> refactorings) {
                    if (!refactorings.isEmpty()) {
                        List refs = new ArrayList<String>();
                        map.put(commitId, refs);
                        for (Refactoring ref : refactorings) {
                            refs.add(ref.getName());
                        }
                    }
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }


    }
}