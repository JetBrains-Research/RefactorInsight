package services;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

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
    return "Refactoring Detection Settings";
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
