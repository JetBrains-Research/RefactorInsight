package data.types.methods;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.InlineOperationRefactoring;
import org.refactoringminer.api.Refactoring;

public class InlineOperationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    InlineOperationRefactoring ref = (InlineOperationRefactoring) refactoring;

    return info.setGroup(Group.METHOD)
        .setElementBefore(ref.getInlinedOperation().getName())
        .setElementAfter(" in method " + ref.getTargetOperationAfterInline().getName())
        .setNameBefore(calculateSignature(ref.getTargetOperationBeforeInline()))
        .setNameAfter(calculateSignature(ref.getTargetOperationAfterInline()))
        .addMarking(ref.getInlinedCodeRangeFromInlinedOperation(),
            ref.getInlinedCodeRangeInTargetOperation());
  }
}
