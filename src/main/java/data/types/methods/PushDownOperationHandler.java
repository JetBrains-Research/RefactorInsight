package data.types.methods;

import data.RefactoringInfo;
import data.Scope;
import data.TrueCodeRange;
import data.types.Handler;
import gr.uom.java.xmi.diff.PushDownOperationRefactoring;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class PushDownOperationHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    PushDownOperationRefactoring ref = (PushDownOperationRefactoring) refactoring;
    return new RefactoringInfo(Scope.METHOD)
        .setType(RefactoringType.PUSH_DOWN_OPERATION)
        .setText(ref.toString())
        .setName(ref.getName())
        .setElementBefore("from class " + ref.getOriginalOperation().getClassName() + ": "
            + ref.getMovedOperation().getName())
        .setElementAfter(" to class "
            + ref.getMovedOperation().getClassName())
        .setLeftSide(
            Arrays.asList(new TrueCodeRange(ref.getSourceOperationCodeRangeBeforeMove())))
        .setRightSide(
            Arrays.asList(new TrueCodeRange(ref.getTargetOperationCodeRangeAfterMove())))
        .setNameBefore(Handler.calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(Handler.calculateSignature(ref.getMovedOperation()));
  }
}
