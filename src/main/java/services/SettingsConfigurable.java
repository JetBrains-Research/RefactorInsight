package services;

import com.intellij.openapi.options.Configurable;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

/**
 * Provides controller functionality for application settings.
 */
public class SettingsConfigurable implements Configurable {
  private SettingsComponent mySettingsComponent;

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
    mySettingsComponent = new SettingsComponent();
    return mySettingsComponent.getPanel();
  }

  @Override
  public boolean isModified() {
    SettingsState settings = SettingsState.getInstance();
    return !(mySettingsComponent.getCommitLimit() == settings.commitLimit
        && mySettingsComponent.getHistoryLimit() == settings.historyLimit
        && mySettingsComponent.getThreads() == settings.threads);
  }

  @Override
  public void apply() {
    SettingsState settings = SettingsState.getInstance();
    settings.commitLimit = mySettingsComponent.getCommitLimit();
    settings.historyLimit = mySettingsComponent.getHistoryLimit();
    settings.threads = mySettingsComponent.getThreads();
  }

  @Override
  public void reset() {
    SettingsState settings = SettingsState.getInstance();
    mySettingsComponent.setCommitLimit(settings.commitLimit);
    mySettingsComponent.setHistoryLimit(settings.historyLimit);
    mySettingsComponent.setThreads(settings.threads);
  }

  @Override
  public void disposeUIResources() {
    mySettingsComponent = null;
  }

}
