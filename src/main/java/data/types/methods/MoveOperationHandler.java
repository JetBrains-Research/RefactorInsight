package data.types.methods;

import data.RefactoringInfo;
import data.Scope;
import data.TrueCodeRange;
import data.types.Handler;
import gr.uom.java.xmi.diff.MoveOperationRefactoring;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class MoveOperationHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    final MoveOperationRefactoring ref = (MoveOperationRefactoring) refactoring;

    if (!ref.getOriginalOperation().getName().equals(ref.getMovedOperation().getName())) {
      return new RefactoringInfo(Scope.METHOD)
          .setType(RefactoringType.MOVE_AND_RENAME_OPERATION)
          .setText(ref.toString())
          .setName(ref.getName())
          .setLeftSide(
              Arrays.asList(new TrueCodeRange(ref.getSourceOperationCodeRangeBeforeMove())))
          .setRightSide(
              Arrays.asList(new TrueCodeRange(ref.getTargetOperationCodeRangeAfterMove())))
          .setElementBefore("from class " + ref.getOriginalOperation().getClassName() + ": "
              + ref.getOriginalOperation().getName())
          .setElementAfter(" to class " + ref.getMovedOperation().getClassName() + ": "
              + ref.getMovedOperation().getName())
          .setNameBefore(Handler.calculateSignature(ref.getOriginalOperation()))
          .setNameAfter(Handler.calculateSignature(ref.getMovedOperation()));
    }

    return new RefactoringInfo(Scope.METHOD)
        .setType(RefactoringType.MOVE_OPERATION)
        .setText(ref.toString())
        .setName(ref.getName())
        .setElementBefore("from class " + ref.getOriginalOperation().getClassName() + ": "
            + ref.getMovedOperation().getName())
        .setElementAfter(" to class "
            + ref.getMovedOperation().getClassName())
        .setLeftSide(Arrays.asList(new TrueCodeRange(ref.getSourceOperationCodeRangeBeforeMove())))
        .setRightSide(Arrays.asList(new TrueCodeRange(ref.getTargetOperationCodeRangeAfterMove())))
        .setNameBefore(Handler.calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(Handler.calculateSignature(ref.getMovedOperation()));
  }
}