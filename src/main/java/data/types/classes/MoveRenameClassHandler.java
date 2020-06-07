package data.types.classes;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.MoveAndRenameClassRefactoring;
import org.refactoringminer.api.Refactoring;

public class MoveRenameClassHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    MoveAndRenameClassRefactoring ref = (MoveAndRenameClassRefactoring) refactoring;

    return info.setGroup(Group.CLASS)
        .addMarking(ref.getOriginalClass().codeRange(), ref.getRenamedClass().codeRange(), true)
        .setNameBefore(ref.getOriginalClassName())
        .setNameAfter(ref.getRenamedClassName())
        .setElementBefore(ref.getOriginalClass().getPackageName())
        .setElementAfter(ref.getRenamedClass().getPackageName());
  }
}
