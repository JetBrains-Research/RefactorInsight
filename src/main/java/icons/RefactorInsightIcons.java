package icons;

import com.intellij.ui.IconManager;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;

public class RefactorInsightIcons {

  private static @NotNull Icon load(@NotNull String path) {
    return IconManager.getInstance().getIcon(path, RefactorInsightIcons.class);
  }

  public static final @NotNull Icon node = load("/icons/refactorInsightNode.svg");
  public static final @NotNull Icon toggle = load("/icons/refactorInsightToggle.svg");
  public static final @NotNull Icon toolWindow = load("/icons/refactorInsightToolWindow.svg");
}
