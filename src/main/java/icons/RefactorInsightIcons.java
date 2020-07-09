package icons;

import com.android.tools.idea.uibuilder.handlers.motion.editor.adapters.Annotations.NotNull;
import com.intellij.ui.IconManager;
import javax.swing.Icon;

public class RefactorInsightIcons {

  private static @NotNull Icon load(@NotNull String path) {
    return IconManager.getInstance().getIcon(path, RefactorInsightIcons.class);
  }

  public static final @NotNull Icon node = load("/icons/refactorInsightNode.svg");
  public static final @NotNull Icon toggle = load("/icons/refactorInsightToggle.svg");
  public static final @NotNull Icon toggle_dark = load("/icons/refactorInsightToggle_dark.svg");
  public static final @NotNull Icon toolWindow = load("/icons/refactorInsightToolWindow.svg");
  public static final @NotNull Icon toolWindow_dark
      = load("/icons/refactorInsightToolWindow_dark.svg");

}
