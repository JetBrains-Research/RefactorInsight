package data.types.methods;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.MoveOperationRefactoring;
import org.refactoringminer.api.Refactoring;

public class MoveOperationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    final MoveOperationRefactoring ref = (MoveOperationRefactoring) refactoring;

    return info.setGroup(Group.METHOD)
        .setElementBefore("from class " + ref.getOriginalOperation().getClassName())
        .setElementAfter(" to class " + ref.getMovedOperation().getClassName())
        .addMarking(ref.getSourceOperationCodeRangeBeforeMove(),
            ref.getTargetOperationCodeRangeAfterMove())
        .setNameBefore(calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(calculateSignature(ref.getMovedOperation()));
  }
}