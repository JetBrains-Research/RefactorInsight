package org.jetbrains.research.refactorinsight.data.types.methods;

import org.jetbrains.research.kotlinrminer.api.Refactoring;
import org.jetbrains.research.kotlinrminer.diff.refactoring.ExtractOperationRefactoring;
import org.jetbrains.research.kotlinrminer.uml.UMLType;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.common.diff.VisualizationType;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ExtractOperationKotlinHandler extends Handler {

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

        String extractedMethod = StringUtils
                .calculateSignatureWithoutClassName(ref.getExtractedOperation().getName(), parameterTypeList);

        if (ref.getRefactoringType()
                == org.jetbrains.research.kotlinrminer.api.RefactoringType.EXTRACT_AND_MOVE_OPERATION) {
            info.setGroup(Group.METHOD)
                    .setThreeSided(true)
                    .setDetailsBefore(classNameBefore)
                    .setDetailsAfter(classNameAfter)
                    .setElementBefore(extractedMethod)
                    .setElementAfter(null)
                    .setNameBefore(StringUtils.calculateSignatureForKotlinMethod(ref.getSourceOperationBeforeExtraction()))
                    .setNameAfter(StringUtils.calculateSignatureForKotlinMethod(ref.getSourceOperationAfterExtraction()))
                    .addMarking(CodeRange.createCodeRangeFromKotlin(ref.getExtractedCodeRangeFromSourceOperation()),
                            CodeRange.createCodeRangeFromKotlin(ref.getExtractedCodeRangeToExtractedOperation()),
                            CodeRange.createCodeRangeFromKotlin(ref.getExtractedCodeRangeFromSourceOperation()),
                            VisualizationType.LEFT,
                            null,
                            RefactoringLine.MarkingOption.NONE,
                            true);

            ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
                    info.addMarking(
                            CodeRange.createCodeRangeFromKotlin(ref.getSourceOperationCodeRangeBeforeExtraction()),
                            CodeRange.createCodeRangeFromKotlin(ref.getExtractedOperation().getBody().getCompositeStatement().codeRange()),
                            CodeRange.createCodeRangeFromKotlin(invocation),
                            VisualizationType.RIGHT,
                            refactoringLine -> refactoringLine.setWord(new String[]{
                                    null,
                                    ref.getExtractedOperation().getName(),
                                    null
                            }),
                            RefactoringLine.MarkingOption.EXTRACT,
                            true));
        } else {
            info.setGroup(Group.METHOD)
                    .setDetailsBefore(classNameBefore)
                    .setDetailsAfter(classNameAfter)
                    .setElementBefore(extractedMethod)
                    .setElementAfter(null)
                    .setNameBefore(StringUtils.calculateSignatureForKotlinMethod(ref.getSourceOperationBeforeExtraction()))
                    .setNameAfter(StringUtils.calculateSignatureForKotlinMethod(ref.getSourceOperationAfterExtraction()))
                    .addMarking(CodeRange.createCodeRangeFromKotlin(ref.getSourceOperationCodeRangeBeforeExtraction()),
                            CodeRange.createCodeRangeFromKotlin(ref.getSourceOperationCodeRangeAfterExtraction()),
                            false)
                    .addMarking(CodeRange.createCodeRangeFromKotlin(ref.getExtractedCodeRangeFromSourceOperation()),
                            CodeRange.createCodeRangeFromKotlin(ref.getExtractedCodeRangeToExtractedOperation()),
                            true);

            ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
                    info.addMarking(
                            CodeRange.createCodeRangeFromKotlin(ref.getExtractedCodeRangeFromSourceOperation()),
                            CodeRange.createCodeRangeFromKotlin(invocation),
                            null,
                            RefactoringLine.MarkingOption.ADD,
                            true)
            );
        }
        return info;
    }
}
