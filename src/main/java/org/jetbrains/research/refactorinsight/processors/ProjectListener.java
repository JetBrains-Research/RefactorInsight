package org.jetbrains.research.refactorinsight.processors;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.utils.Utils;

/**
 * ProjectListener that disposes objects that can cause memory leaks.
 */
public class ProjectListener implements ProjectManagerListener {

  @Override
  public void projectClosing(@NotNull Project project) {
    Utils.dispose();
    if (Utils.manager != null) {
      Utils.manager = null;
    }
  }
}
