package data.types.methods;

import data.RefactoringEntry;
import data.RefactoringInfo;
import data.TrueCodeRange;
import data.Type;
import data.types.Handler;
import gr.uom.java.xmi.diff.PullUpOperationRefactoring;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class PullUpOperationHandler implements Handler {


  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    PullUpOperationRefactoring ref = (PullUpOperationRefactoring) refactoring;
    return new RefactoringInfo(Type.METHOD)
        .setType(RefactoringType.PULL_UP_OPERATION)
        .setText(ref.toString())
        .setName(ref.getName())
        .setLeftSide(
            Arrays.asList(new TrueCodeRange(ref.getSourceOperationCodeRangeBeforeMove())))
        .setRightSide(
            Arrays.asList(new TrueCodeRange(ref.getTargetOperationCodeRangeAfterMove())))
        .setNameBefore(Handler.calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(Handler.calculateSignature(ref.getMovedOperation()));
  }
}
