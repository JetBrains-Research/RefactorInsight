package org.jetbrains.research.refactorinsight.kotlin.impl.data.methods;

import org.jetbrains.research.kotlinrminer.ide.Refactoring;
import org.jetbrains.research.kotlinrminer.ide.diff.refactoring.InlineOperationRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.FoldingBuilder;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.KotlinRefactoringHandler;

import static org.jetbrains.research.refactorinsight.kotlin.impl.data.util.KotlinUtils.calculateSignatureForKotlinMethod;
import static org.jetbrains.research.refactorinsight.kotlin.impl.data.util.KotlinUtils.createCodeRangeFromKotlin;

public class InlineOperationKotlinHandler extends KotlinRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring,
                                   RefactoringInfo info) {
        InlineOperationRefactoring ref =
                (InlineOperationRefactoring) refactoring;

        ref.getInlinedOperationInvocations().forEach(c ->
                info.addMarking(createCodeRangeFromKotlin(c.codeRange(), info),
                        createCodeRangeFromKotlin(ref.getInlinedCodeRangeInTargetOperation(), info),
                        true));
        String classNameBefore = ref.getTargetOperationBeforeInline().getClassName();
        String classNameAfter = ref.getTargetOperationAfterInline().getClassName();

        info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setElementBefore(ref.getInlinedOperation().getName())
                .setElementAfter(null)
                .setNameBefore(calculateSignatureForKotlinMethod(ref.getTargetOperationBeforeInline()))
                .setNameAfter(calculateSignatureForKotlinMethod(ref.getTargetOperationAfterInline()))
                .addMarking(createCodeRangeFromKotlin(ref.getTargetOperationCodeRangeBeforeInline(), info),
                        createCodeRangeFromKotlin(ref.getTargetOperationCodeRangeAfterInline(), info),
                        false);

        info.setFoldingDescriptorMid(FoldingBuilder.fromMethod(ref.getInlinedOperation()));

        if (ref.getInlinedOperation().codeRange().getFilePath()
                .equals(ref.getTargetOperationAfterInline().codeRange().getFilePath())) {
            info.addMarking(createCodeRangeFromKotlin(ref.getInlinedOperationCodeRange(), info),
                    createCodeRangeFromKotlin(ref.getInlinedCodeRangeInTargetOperation(), info),
                    null,
                    RefactoringLine.MarkingOption.REMOVE,
                    false);
        }
        return info;
    }

}
