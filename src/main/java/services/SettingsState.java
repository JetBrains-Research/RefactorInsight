package services;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Supports storing the application settings in a persistent way.
 * The State and Storage annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(
    name = "SettingsState",
    storages = {@Storage("SdkSettingsPlugin.xml")}
)
public class SettingsState implements PersistentStateComponent<SettingsState> {

  public int commitLimit = 100;
  public int historyLimit = 100;
  public int threads = 8;

  public static SettingsState getInstance() {
    return ServiceManager.getService(SettingsState.class);
  }

  @Nullable
  @Override
  public SettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull SettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

}