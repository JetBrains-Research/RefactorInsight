package org.jetbrains.research.refactorinsight.data.types.methods;

import org.jetbrains.research.kotlinrminer.api.Refactoring;
import org.jetbrains.research.kotlinrminer.diff.refactoring.InlineOperationRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;

public class InlineOperationKotlinHandler extends Handler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring,
                                   RefactoringInfo info) {
        InlineOperationRefactoring ref =
                (InlineOperationRefactoring) refactoring;

        ref.getInlinedOperationInvocations().forEach(c ->
                info.addMarking(CodeRange.createCodeRangeFromKotlin(c.codeRange()),
                        CodeRange.createCodeRangeFromKotlin(ref.getInlinedCodeRangeInTargetOperation()),
                        true));
        String classNameBefore = ref.getTargetOperationBeforeInline().getClassName();
        String classNameAfter = ref.getTargetOperationAfterInline().getClassName();

        info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setElementBefore(ref.getInlinedOperation().getName())
                .setElementAfter(null)
                .setNameBefore(StringUtils.calculateSignatureForKotlinMethod(ref.getTargetOperationBeforeInline()))
                .setNameAfter(StringUtils.calculateSignatureForKotlinMethod(ref.getTargetOperationAfterInline()))
                .addMarking(CodeRange.createCodeRangeFromKotlin(ref.getTargetOperationCodeRangeBeforeInline()),
                        CodeRange.createCodeRangeFromKotlin(ref.getTargetOperationCodeRangeAfterInline()),
                        false);

        if (ref.getInlinedOperation().codeRange().getFilePath()
                .equals(ref.getTargetOperationAfterInline().codeRange().getFilePath())) {
            info.addMarking(CodeRange.createCodeRangeFromKotlin(ref.getInlinedOperationCodeRange()),
                    CodeRange.createCodeRangeFromKotlin(ref.getInlinedCodeRangeInTargetOperation()),
                    null,
                    RefactoringLine.MarkingOption.REMOVE,
                    false);
        }
        return info;
    }

}
