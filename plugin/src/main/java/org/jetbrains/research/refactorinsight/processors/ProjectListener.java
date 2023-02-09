package org.jetbrains.research.refactorinsight.processors;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.utils.VcsUtils;

/**
 * ProjectListener that disposes objects that can cause memory leaks.
 */
public class ProjectListener implements ProjectManagerListener {

  @Override
  public void projectClosing(@NotNull Project project) {
    VcsUtils.dispose();
    if (VcsUtils.manager != null) {
      VcsUtils.manager = null;
    }
  }
}
