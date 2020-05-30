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
    int index = Utils.indexOfDifference(classBefore, classAfter);
    return info.setGroup(Group.METHOD)
        .setElementBefore("from class " + classBefore.substring(index))
        .setElementAfter("to class " + classAfter.substring(index))
        .addMarking(ref.getSourceOperationCodeRangeBeforeMove(),
            ref.getTargetOperationCodeRangeAfterMove())
        .setNameBefore(calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(calculateSignature(ref.getMovedOperation()));
  }
}
