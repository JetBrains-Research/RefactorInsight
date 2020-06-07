package data.types.methods;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.PushDownOperationRefactoring;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

public class PushDownOperationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    PushDownOperationRefactoring ref = (PushDownOperationRefactoring) refactoring;
    String classBefore = ref.getOriginalOperation().getClassName();
    String classAfter = ref.getMovedOperation().getClassName();

    return info.setGroup(Group.METHOD)
        .setDetailsBefore(classBefore)
        .setDetailsAfter(classAfter)
        .addMarking(ref.getSourceOperationCodeRangeBeforeMove(),
            ref.getTargetOperationCodeRangeAfterMove(), true)
        .setNameBefore(Utils.calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(Utils.calculateSignature(ref.getMovedOperation()));
  }
}
