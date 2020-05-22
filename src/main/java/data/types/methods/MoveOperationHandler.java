package data.types.methods;

import data.RefactoringEntry;
import data.RefactoringInfo;
import data.TrueCodeRange;
import data.Type;
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
      return new RefactoringInfo(Type.METHOD)
          .setType(RefactoringType.MOVE_AND_RENAME_OPERATION)
          .setText(ref.toString())
          .setName(ref.getName())
          .setLeftSide(
              Arrays.asList(new TrueCodeRange(ref.getSourceOperationCodeRangeBeforeMove())))
          .setRightSide(
              Arrays.asList(new TrueCodeRange(ref.getTargetOperationCodeRangeAfterMove())))
          .setNameBefore(Handler.calculateSignature(ref.getOriginalOperation()))
          .setNameAfter(Handler.calculateSignature(ref.getMovedOperation()));
    }

    return new RefactoringInfo(Type.METHOD)
        .setType(RefactoringType.MOVE_OPERATION)
        .setText(ref.toString())
        .setName(ref.getName())
        .setLeftSide(Arrays.asList(new TrueCodeRange(ref.getSourceOperationCodeRangeBeforeMove())))
        .setRightSide(Arrays.asList(new TrueCodeRange(ref.getTargetOperationCodeRangeAfterMove())))
        .setNameBefore(Handler.calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(Handler.calculateSignature(ref.getMovedOperation()));
  }
}