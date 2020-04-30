import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.VcsRoot;
import com.intellij.openapi.vcs.impl.ProjectLevelVcsManagerImpl;
import org.refactoringminer.api.*;
import git4idea.GitCommit;
import git4idea.GitReference;
import git4idea.history.GitHistoryUtils;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RefactoringAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var currentProject = e.getProject();
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
                    System.out.println("Refactorings at " + commitId);
                    for (Refactoring ref : refactorings) {
                        System.out.println(ref.toString());
                    }
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }


    }
}