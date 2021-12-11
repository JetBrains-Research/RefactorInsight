package org.jetbrains.research.refactorinsight.kotlin.impl.data.methods;

import org.jetbrains.research.kotlinrminer.api.Refactoring;
import org.jetbrains.research.kotlinrminer.diff.refactoring.ExtractOperationRefactoring;
import org.jetbrains.research.kotlinrminer.uml.UMLType;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.common.diff.VisualizationType;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;
import org.jetbrains.research.refactorinsight.kotlin.api.KotlinRefactoringHandler;

import java.util.ArrayList;
import java.util.List;

import static org.jetbrains.research.refactorinsight.kotlin.api.util.Utils.calculateSignatureForKotlinMethod;
import static org.jetbrains.research.refactorinsight.kotlin.api.util.Utils.createCodeRangeFromKotlin;

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
                    .setNameBefore(calculateSignatureForKotlinMethod(ref.getSourceOperationBeforeExtraction()))
                    .setNameAfter(calculateSignatureForKotlinMethod(ref.getSourceOperationAfterExtraction()))
                    .addMarking(createCodeRangeFromKotlin(ref.getExtractedCodeRangeFromSourceOperation()),
                            createCodeRangeFromKotlin(ref.getExtractedCodeRangeToExtractedOperation()),
                            createCodeRangeFromKotlin(ref.getExtractedCodeRangeFromSourceOperation()),
                            VisualizationType.LEFT,
                            null,
                            RefactoringLine.MarkingOption.NONE,
                            true);

            ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
                    info.addMarking(
                            createCodeRangeFromKotlin(ref.getSourceOperationCodeRangeBeforeExtraction()),
                            createCodeRangeFromKotlin(ref.getExtractedOperation().getBody().getCompositeStatement().codeRange()),
                            createCodeRangeFromKotlin(invocation),
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
                    .setNameBefore(calculateSignatureForKotlinMethod(ref.getSourceOperationBeforeExtraction()))
                    .setNameAfter(calculateSignatureForKotlinMethod(ref.getSourceOperationAfterExtraction()))
                    .addMarking(createCodeRangeFromKotlin(ref.getSourceOperationCodeRangeBeforeExtraction()),
                            createCodeRangeFromKotlin(ref.getSourceOperationCodeRangeAfterExtraction()),
                            false)
                    .addMarking(createCodeRangeFromKotlin(ref.getExtractedCodeRangeFromSourceOperation()),
                            createCodeRangeFromKotlin(ref.getExtractedCodeRangeToExtractedOperation()),
                            true);

            ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
                    info.addMarking(
                            createCodeRangeFromKotlin(ref.getExtractedCodeRangeFromSourceOperation()),
                            createCodeRangeFromKotlin(invocation),
                            null,
                            RefactoringLine.MarkingOption.ADD,
                            true)
            );
        }
        return info;
    }
}
