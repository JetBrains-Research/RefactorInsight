package processors;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;
import utils.Utils;

public class ProjectListener implements ProjectManagerListener {

  @Override
  public void projectClosing(@NotNull Project project) {
    Utils.dispose();
  }
}
