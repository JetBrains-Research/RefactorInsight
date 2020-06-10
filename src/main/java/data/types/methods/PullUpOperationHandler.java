package data.types.methods;

import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.PullUpOperationRefactoring;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

public class PullUpOperationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    PullUpOperationRefactoring ref = (PullUpOperationRefactoring) refactoring;
    String classBefore = ref.getOriginalOperation().getClassName();
    String classAfter = ref.getMovedOperation().getClassName();

    return info.setGroup(RefactoringInfo.Group.METHOD)
        .setDetailsBefore(classBefore)
        .setDetailsAfter(classAfter)
        .addMarking(ref.getSourceOperationCodeRangeBeforeMove(),
            ref.getTargetOperationCodeRangeAfterMove(), true)
        .setNameBefore(Utils.calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(Utils.calculateSignature(ref.getMovedOperation()));
  }
}
