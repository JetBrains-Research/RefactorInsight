package org.jetbrains.research.refactorinsight.data.types.methods;

import gr.uom.java.xmi.diff.InlineOperationRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.jetbrains.research.refactorinsight.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

public class InlineOperationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    InlineOperationRefactoring ref = (InlineOperationRefactoring) refactoring;

    ref.getInlinedOperationInvocations().forEach(c -> {
      info.addMarking(c.codeRange(), ref.getInlinedCodeRangeInTargetOperation(), true);
    });
    String classNameBefore = ref.getTargetOperationBeforeInline().getClassName();
    String classNameAfter = ref.getTargetOperationAfterInline().getClassName();

    info.setGroup(Group.METHOD)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setElementBefore(ref.getInlinedOperation().getName())
        .setElementAfter(null)
        .setNameBefore(StringUtils.calculateSignature(ref.getTargetOperationBeforeInline()))
        .setNameAfter(StringUtils.calculateSignature(ref.getTargetOperationAfterInline()))
        .addMarking(ref.getTargetOperationCodeRangeBeforeInline(),
            ref.getTargetOperationCodeRangeAfterInline(), false);

    if (ref.getInlinedOperation().codeRange().getFilePath()
        .equals(ref.getTargetOperationAfterInline().codeRange().getFilePath())) {
      info.addMarking(ref.getInlinedOperationCodeRange(),
          ref.getInlinedCodeRangeInTargetOperation(),
          null,
          RefactoringLine.MarkingOption.REMOVE,
          false);
    }
    return info;
  }
}
