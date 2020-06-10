package data.types.classes;

import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.MoveClassRefactoring;
import org.refactoringminer.api.Refactoring;

public class MoveClassHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    MoveClassRefactoring ref = (MoveClassRefactoring) refactoring;

    if (ref.getMovedClass().isAbstract()) {
      info.setGroup(RefactoringInfo.Group.ABSTRACT);
    } else if (ref.getMovedClass().isInterface()) {
      info.setGroup(RefactoringInfo.Group.INTERFACE);
    } else {
      info.setGroup(RefactoringInfo.Group.CLASS);
    }
    return info
        .addMarking(ref.getOriginalClass().codeRange(), ref.getMovedClass().codeRange(), true)
        .setNameBefore(ref.getOriginalClassName())
        .setNameAfter(ref.getMovedClassName())
        .setDetailsBefore(ref.getOriginalClass().getPackageName())
        .setDetailsAfter(ref.getMovedClass().getPackageName());
  }
}
