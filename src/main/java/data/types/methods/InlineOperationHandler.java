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

    //TODO what is this MOVE AND INLINE?

    return info.setGroup(Group.METHOD)
        .setNameBefore(calculateSignature(ref.getTargetOperationBeforeInline()))
        .setNameAfter(calculateSignature(ref.getTargetOperationAfterInline()));
  }
}
