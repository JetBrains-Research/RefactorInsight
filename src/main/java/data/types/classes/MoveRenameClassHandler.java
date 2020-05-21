package data.types.classes;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.MoveAndRenameClassRefactoring;
import org.refactoringminer.api.Refactoring;

public class MoveRenameClassHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    MoveAndRenameClassRefactoring ref = (MoveAndRenameClassRefactoring) refactoring;

    return info.setGroup(Group.CLASS)
        .addMarking(ref.getOriginalClass().codeRange(), ref.getRenamedClass().codeRange())
        .setNameBefore(ref.getOriginalClassName())
        .setNameAfter(ref.getRenamedClassName());
  }
}
