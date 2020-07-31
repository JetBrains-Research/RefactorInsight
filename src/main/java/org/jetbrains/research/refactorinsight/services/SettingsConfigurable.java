package org.jetbrains.research.refactorinsight.services;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import java.util.List;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.research.refactorinsight.RefactorInsightBundle;
import org.jetbrains.research.refactorinsight.ui.windows.SettingsComponent;

/**
 * Provides controller functionality for application settings.
 */
public class SettingsConfigurable implements Configurable {
  private SettingsComponent mySettingsComponent;
  private Project project;

  public SettingsConfigurable(Project project) {
    this.project = project;
  }

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return RefactorInsightBundle.message("setting");
  }

  @Override
  public JComponent getPreferredFocusedComponent() {
    return mySettingsComponent.getPreferredFocusedComponent();
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    mySettingsComponent = new SettingsComponent(project);
    return mySettingsComponent.getPanel();
  }

  @Override
  public boolean isModified() {
    SettingsState settings = SettingsState.getInstance(project);
    return !(mySettingsComponent.getCommitLimit() == settings.commitLimit
        && mySettingsComponent.getHistoryLimit() == settings.historyLimit
        && mySettingsComponent.getThreads() == settings.threads);
  }

  @Override
  public void apply() {
    SettingsState settings = SettingsState.getInstance(project);
    settings.commitLimit = mySettingsComponent.getCommitLimit();
    settings.historyLimit = mySettingsComponent.getHistoryLimit();
    settings.threads = mySettingsComponent.getThreads();
    List<GitRepository> repositories = GitRepositoryManager
        .getInstance(project).getRepositories();
    if (repositories.isEmpty()) {
      return;
    }
    MiningService.getInstance(project).mineRepo(repositories.get(0));
  }

  @Override
  public void reset() {
    SettingsState settings = SettingsState.getInstance(project);
    mySettingsComponent.setCommitLimit(settings.commitLimit);
    mySettingsComponent.setHistoryLimit(settings.historyLimit);
    mySettingsComponent.setThreads(settings.threads);
  }

  @Override
  public void disposeUIResources() {
    mySettingsComponent = null;
  }

}
