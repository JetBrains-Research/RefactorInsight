package data.types.methods;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.PullUpOperationRefactoring;
import org.refactoringminer.api.Refactoring;

public class PullUpOperationHandler extends Handler {


  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    PullUpOperationRefactoring ref = (PullUpOperationRefactoring) refactoring;
    //TODO check ammount of files
    return info.setGroup(Group.METHOD)
        .addMarking(ref.getSourceOperationCodeRangeBeforeMove(),
            ref.getTargetOperationCodeRangeAfterMove())
        .setNameBefore(calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(calculateSignature(ref.getMovedOperation()));
  }
}
