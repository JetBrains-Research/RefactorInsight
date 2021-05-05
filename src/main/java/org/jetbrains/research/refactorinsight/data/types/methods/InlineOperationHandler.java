package org.jetbrains.research.refactorinsight.data.types.methods;

import gr.uom.java.xmi.diff.InlineOperationRefactoring;
import org.jetbrains.research.refactorinsight.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.jetbrains.research.refactorinsight.folding.FoldingPositions;
import org.jetbrains.research.refactorinsight.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

public class InlineOperationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    InlineOperationRefactoring ref = (InlineOperationRefactoring) refactoring;

    info.setFoldingPositionsBefore(FoldingPositions.fromMethod(ref.getTargetOperationBeforeInline()));
    info.setFoldingPositionsMid(FoldingPositions.fromMethod(ref.getInlinedOperation()));
    info.setFoldingPositionsAfter(FoldingPositions.fromMethod(ref.getTargetOperationAfterInline()));

    ref.getInlinedOperationInvocations().forEach(c ->
        info.addMarking(new CodeRange(c.codeRange()), new CodeRange(ref.getInlinedCodeRangeInTargetOperation()), true));
    String classNameBefore = ref.getTargetOperationBeforeInline().getClassName();
    String classNameAfter = ref.getTargetOperationAfterInline().getClassName();

    info.setGroup(Group.METHOD)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setElementBefore(ref.getInlinedOperation().getName())
        .setElementAfter(null)
        .setNameBefore(StringUtils.calculateSignature(ref.getTargetOperationBeforeInline()))
        .setNameAfter(StringUtils.calculateSignature(ref.getTargetOperationAfterInline()))
        .addMarking(new CodeRange(ref.getTargetOperationCodeRangeBeforeInline()),
            new CodeRange(ref.getTargetOperationCodeRangeAfterInline()), false);

    if (ref.getInlinedOperation().codeRange().getFilePath()
        .equals(ref.getTargetOperationAfterInline().codeRange().getFilePath())) {
      info.addMarking(new CodeRange(ref.getInlinedOperationCodeRange()),
          new CodeRange(ref.getInlinedCodeRangeInTargetOperation()),
          null,
          RefactoringLine.MarkingOption.REMOVE,
          false);
    }
    return info;
  }

  @Override
  public RefactoringInfo specify(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring,
                                 RefactoringInfo info) {
    org.jetbrains.research.kotlinrminer.diff.refactoring.InlineOperationRefactoring ref =
        (org.jetbrains.research.kotlinrminer.diff.refactoring.InlineOperationRefactoring) refactoring;

    info.setFoldingPositionsBefore(FoldingPositions.fromMethod(ref.getTargetOperationBeforeInline()));
    info.setFoldingPositionsMid(FoldingPositions.fromMethod(ref.getInlinedOperation()));
    info.setFoldingPositionsAfter(FoldingPositions.fromMethod(ref.getTargetOperationAfterInline()));

    ref.getInlinedOperationInvocations().forEach(c ->
        info.addMarking(new CodeRange(c.codeRange()), new CodeRange(ref.getInlinedCodeRangeInTargetOperation()), true));
    String classNameBefore = ref.getTargetOperationBeforeInline().getClassName();
    String classNameAfter = ref.getTargetOperationAfterInline().getClassName();

    info.setGroup(Group.METHOD)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setElementBefore(ref.getInlinedOperation().getName())
        .setElementAfter(null)
        .setNameBefore(StringUtils.calculateSignature(ref.getTargetOperationBeforeInline()))
        .setNameAfter(StringUtils.calculateSignature(ref.getTargetOperationAfterInline()))
        .addMarking(new CodeRange(ref.getTargetOperationCodeRangeBeforeInline()),
            new CodeRange(ref.getTargetOperationCodeRangeAfterInline()), false);

    if (ref.getInlinedOperation().codeRange().getFilePath()
        .equals(ref.getTargetOperationAfterInline().codeRange().getFilePath())) {
      info.addMarking(new CodeRange(ref.getInlinedOperationCodeRange()),
          new CodeRange(ref.getInlinedCodeRangeInTargetOperation()),
          null,
          RefactoringLine.MarkingOption.REMOVE,
          false);
    }
    return info;
  }
}
