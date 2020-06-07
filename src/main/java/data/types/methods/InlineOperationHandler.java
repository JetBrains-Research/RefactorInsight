package data.types.methods;

import data.Group;
import data.RefactoringInfo;
import data.RefactoringLine;
import data.types.Handler;
import gr.uom.java.xmi.diff.InlineOperationRefactoring;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

public class InlineOperationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    InlineOperationRefactoring ref = (InlineOperationRefactoring) refactoring;

    ref.getInlinedOperationInvocations().forEach(c -> {
      info.addMarking(c.codeRange(), ref.getInlinedCodeRangeInTargetOperation(), true);
    });
    String classNameBefore = ref.getTargetOperationBeforeInline().getClassName();
    String classNameAfter = ref.getTargetOperationAfterInline().getClassName();

    return info.setGroup(Group.METHOD)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setElementBefore(Utils.calculateSignature(ref.getInlinedOperation()))
        .setElementAfter(null)
        .setNameBefore(Utils.calculateSignature(ref.getTargetOperationBeforeInline()))
        .setNameAfter(Utils.calculateSignature(ref.getTargetOperationAfterInline()))
        .addMarking(ref.getInlinedOperationCodeRange(),
            ref.getInlinedCodeRangeInTargetOperation(),
            null,
            RefactoringLine.MarkingOption.REMOVE,
            false);

  }
}
