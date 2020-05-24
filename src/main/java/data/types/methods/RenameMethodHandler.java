package data.types.methods;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.RenameOperationRefactoring;
import org.refactoringminer.api.Refactoring;

public class RenameMethodHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RenameOperationRefactoring ref = (RenameOperationRefactoring) refactoring;
    return info.setGroup(Group.METHOD)
        .setElementBefore(null)
        .setElementAfter(null)
        .addMarking(ref.getSourceOperationCodeRangeBeforeRename(),
            ref.getTargetOperationCodeRangeAfterRename())
        .setNameBefore(calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(calculateSignature(ref.getRenamedOperation()));
  }
}
