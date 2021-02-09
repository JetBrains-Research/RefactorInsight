package org.jetbrains.research.refactorinsight.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.services.MiningService;
import org.jetbrains.research.refactorinsight.RefactorInsightBundle;

/**
 * Represents the `Mine All Refactorings` action.
 * It retrieves the Git repository of the current project iff it exists.
 * If the currently opened project is not a git repository,
 * an error message is shown.
 * Calls the {@link MiningService} in order to mine all commits.
 */
public class RefactoringAction extends AnAction {

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    final List<GitRepository> repositories = GitRepositoryManager
        .getInstance(e.getProject()).getRepositories();
    if (repositories.isEmpty()) {
      Messages.showErrorDialog(RefactorInsightBundle.message("no.repo"),
                               RefactorInsightBundle.message("name"));
      return;
    }
    GitRepository repository = repositories.get(0);
    MiningService.getInstance(e.getProject()).clear();
    MiningService.getInstance(e.getProject()).mineAll(repository);
  }

}