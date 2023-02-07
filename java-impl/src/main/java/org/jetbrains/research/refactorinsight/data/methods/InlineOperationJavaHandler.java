package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.diff.InlineOperationRefactoring;
import org.jetbrains.research.refactorinsight.data.*;
import org.jetbrains.research.refactorinsight.data.util.JavaUtils;
import org.refactoringminer.api.Refactoring;

public class InlineOperationJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        InlineOperationRefactoring ref = (InlineOperationRefactoring) refactoring;

        ref.getInlinedOperationInvocations().forEach(c ->
                info.addMarking(JavaUtils.createCodeRangeFromJava(c.codeRange()),
                        JavaUtils.createCodeRangeFromJava(ref.getInlinedCodeRangeInTargetOperation()),
                        true));
        String classNameBefore = ref.getTargetOperationBeforeInline().getClassName();
        String classNameAfter = ref.getTargetOperationAfterInline().getClassName();

        info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setElementBefore(ref.getInlinedOperation().getName())
                .setElementAfter(null)
                .setNameBefore(JavaUtils.calculateSignatureForVariableDeclarationContainer(ref.getTargetOperationBeforeInline()))
                .setNameAfter(JavaUtils.calculateSignatureForVariableDeclarationContainer(ref.getTargetOperationAfterInline()))
                .addMarking(JavaUtils.createCodeRangeFromJava(ref.getTargetOperationCodeRangeBeforeInline()),
                        JavaUtils.createCodeRangeFromJava(ref.getTargetOperationCodeRangeAfterInline()),
                        false);

        info.setFoldingDescriptorMid(FoldingBuilder.fromMethod(ref.getInlinedOperation()));

        if (ref.getInlinedOperation().codeRange().getFilePath()
                .equals(ref.getTargetOperationAfterInline().codeRange().getFilePath())) {
            info.addMarking(JavaUtils.createCodeRangeFromJava(ref.getInlinedOperationCodeRange()),
                    JavaUtils.createCodeRangeFromJava(ref.getInlinedCodeRangeInTargetOperation()),
                    null,
                    RefactoringLine.MarkingOption.REMOVE,
                    false);
        }
        return info;
    }

}
