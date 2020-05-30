package processors;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.NotNull;
import ui.MethodRefactoringToolbar;

public class ProjectListener implements ProjectManagerListener {

  @Override
  public void projectClosing(@NotNull Project project) {
    Disposer.dispose(MethodRefactoringToolbar.openLogTab);
  }
}
