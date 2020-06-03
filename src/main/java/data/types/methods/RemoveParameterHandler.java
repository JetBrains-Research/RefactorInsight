package data.types.methods;

import com.intellij.openapi.project.Project;
import data.RefactoringInfo;
import data.types.Handler;
import org.refactoringminer.api.Refactoring;

public class RemoveParameterHandler extends Handler {
  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    return null;
  }
}
