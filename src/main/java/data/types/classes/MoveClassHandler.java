package data.types.classes;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.MoveClassRefactoring;
import org.refactoringminer.api.Refactoring;

public class MoveClassHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    MoveClassRefactoring ref = (MoveClassRefactoring) refactoring;

    return info.setGroup(Group.CLASS)
        .addMarking(ref.getOriginalClass().codeRange(), ref.getMovedClass().codeRange(), true)
        .setNameBefore(ref.getOriginalClassName())
        .setNameAfter(ref.getMovedClassName())
        .setElementBefore(ref.getOriginalClass().getPackageName())
        .setElementAfter(ref.getMovedClass().getPackageName());
  }
}
