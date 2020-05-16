package data.types.methods;

import data.RefactoringInfo;
import data.TrueCodeRange;
import data.Type;
import data.types.Handler;
import gr.uom.java.xmi.diff.PushDownOperationRefactoring;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class PushDownOperationHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring, String commitId) {
    PushDownOperationRefactoring ref = (PushDownOperationRefactoring) refactoring;
    return new RefactoringInfo(Type.METHOD)
        .setType(RefactoringType.PUSH_DOWN_OPERATION)
        .setText(ref.toString())
        .setName(ref.getName())
        .setCommitId(commitId)
        .setLeftSide(
            Arrays.asList(new TrueCodeRange(ref.getSourceOperationCodeRangeBeforeMove())))
        .setRightSide(
            Arrays.asList(new TrueCodeRange(ref.getTargetOperationCodeRangeAfterMove())))
        .setNameBefore(Handler.calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(Handler.calculateSignature(ref.getMovedOperation()));
  }
}
