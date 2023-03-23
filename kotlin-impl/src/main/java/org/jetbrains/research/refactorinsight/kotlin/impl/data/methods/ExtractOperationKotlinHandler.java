package org.jetbrains.research.refactorinsight.kotlin.impl.data.methods;

import org.jetbrains.research.kotlinrminer.ide.Refactoring;
import org.jetbrains.research.kotlinrminer.ide.diff.refactoring.ExtractOperationRefactoring;
import org.jetbrains.research.kotlinrminer.ide.uml.UMLType;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.FoldingBuilder;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.KotlinRefactoringHandler;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.util.KotlinUtils;
import org.jetbrains.research.refactorinsight.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ExtractOperationKotlinHandler extends KotlinRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring,
                                   RefactoringInfo info) {
        ExtractOperationRefactoring ref =
                (ExtractOperationRefactoring) refactoring;

        String classNameBefore = ref.getSourceOperationBeforeExtraction().getClassName();
        String classNameAfter = ref.getExtractedOperation().getClassName();

        List<String> parameterTypeList = new ArrayList<>();
        for (UMLType type : ref.getExtractedOperation().getParameterTypeList()) {
            parameterTypeList.add(type.toString());
        }

        info.setFoldingDescriptorMid(FoldingBuilder.fromMethod(ref.getExtractedOperation()));

        String extractedMethod = StringUtils
                .calculateSignatureWithoutClassName(ref.getExtractedOperation().getName(), parameterTypeList);

        if (ref.getRefactoringType()
                == org.jetbrains.research.kotlinrminer.common.RefactoringType.EXTRACT_AND_MOVE_OPERATION) {
            info.setGroup(Group.METHOD)
                    .setDetailsBefore(classNameBefore)
                    .setDetailsAfter(classNameAfter)
                    .setElementBefore(ref.getSourceOperationAfterExtraction().getName())
                    .setElementAfter(extractedMethod)
                    .setNameBefore(KotlinUtils.calculateSignatureForKotlinMethod(ref.getSourceOperationAfterExtraction()))
                    .setNameAfter(KotlinUtils.calculateSignatureForKotlinMethod(ref.getExtractedOperation()))
                    .addMarking(KotlinUtils.createCodeRangeFromKotlin(ref.getSourceOperationAfterExtraction().codeRange(), info),
                            KotlinUtils.createCodeRangeFromKotlin(ref.getExtractedOperationCodeRange(), info),
                            null,
                            RefactoringLine.MarkingOption.ADD,
                            true);

            ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
                    info.addMarking(
                            KotlinUtils.createCodeRangeFromKotlin(invocation, info),
                            KotlinUtils.createCodeRangeFromKotlin(ref.getExtractedOperationCodeRange(), info),
                            null,
                            RefactoringLine.MarkingOption.ADD,
                            true)
            );
        } else {
            info.setGroup(Group.METHOD)
                    .setDetailsBefore(classNameBefore)
                    .setDetailsAfter(classNameAfter)
                    .setElementBefore(extractedMethod)
                    .setElementAfter(null)
                    .setNameBefore(KotlinUtils.calculateSignatureForKotlinMethod(ref.getSourceOperationBeforeExtraction()))
                    .setNameAfter(KotlinUtils.calculateSignatureForKotlinMethod(ref.getSourceOperationAfterExtraction()))
                    .addMarking(KotlinUtils.createCodeRangeFromKotlin(ref.getSourceOperationCodeRangeBeforeExtraction(), info),
                            KotlinUtils.createCodeRangeFromKotlin(ref.getSourceOperationCodeRangeAfterExtraction(), info),
                            false)
                    .addMarking(KotlinUtils.createCodeRangeFromKotlin(ref.getExtractedCodeRangeFromSourceOperation(), info),
                            KotlinUtils.createCodeRangeFromKotlin(ref.getExtractedOperationCodeRange(), info),
                            null,
                            RefactoringLine.MarkingOption.ADD,
                            true);

            ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
                    info.addMarking(
                            KotlinUtils.createCodeRangeFromKotlin(ref.getExtractedCodeRangeFromSourceOperation(), info),
                            KotlinUtils.createCodeRangeFromKotlin(invocation, info),
                            null,
                            RefactoringLine.MarkingOption.ADD,
                            true)
            );
        }
        return info;
    }
}
