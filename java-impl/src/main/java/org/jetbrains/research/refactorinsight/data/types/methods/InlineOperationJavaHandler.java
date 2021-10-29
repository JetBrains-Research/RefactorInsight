package org.jetbrains.research.refactorinsight.data.types.methods;

import gr.uom.java.xmi.diff.InlineOperationRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

public class InlineOperationJavaHandler extends Handler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        InlineOperationRefactoring ref = (InlineOperationRefactoring) refactoring;

        ref.getInlinedOperationInvocations().forEach(c ->
                info.addMarking(CodeRange.createCodeRangeFromJava(c.codeRange()),
                        CodeRange.createCodeRangeFromJava(ref.getInlinedCodeRangeInTargetOperation()),
                        true));
        String classNameBefore = ref.getTargetOperationBeforeInline().getClassName();
        String classNameAfter = ref.getTargetOperationAfterInline().getClassName();

        info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setElementBefore(ref.getInlinedOperation().getName())
                .setElementAfter(null)
                .setNameBefore(StringUtils.calculateSignatureForJavaMethod(ref.getTargetOperationBeforeInline()))
                .setNameAfter(StringUtils.calculateSignatureForJavaMethod(ref.getTargetOperationAfterInline()))
                .addMarking(CodeRange.createCodeRangeFromJava(ref.getTargetOperationCodeRangeBeforeInline()),
                        CodeRange.createCodeRangeFromJava(ref.getTargetOperationCodeRangeAfterInline()),
                        false);

        if (ref.getInlinedOperation().codeRange().getFilePath()
                .equals(ref.getTargetOperationAfterInline().codeRange().getFilePath())) {
            info.addMarking(CodeRange.createCodeRangeFromJava(ref.getInlinedOperationCodeRange()),
                    CodeRange.createCodeRangeFromJava(ref.getInlinedCodeRangeInTargetOperation()),
                    null,
                    RefactoringLine.MarkingOption.REMOVE,
                    false);
        }
        return info;
    }

}
