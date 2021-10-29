package org.jetbrains.research.refactorinsight.data.types.methods;

import gr.uom.java.xmi.UMLType;
import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.common.diff.VisualizationType;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

import java.util.ArrayList;
import java.util.List;

public class ExtractOperationJavaHandler extends Handler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ExtractOperationRefactoring ref = (ExtractOperationRefactoring) refactoring;
        String classNameBefore = ref.getSourceOperationBeforeExtraction().getClassName();
        String classNameAfter = ref.getExtractedOperation().getClassName();

        List<String> parameterTypeList = new ArrayList<>();
        for (UMLType type : ref.getExtractedOperation().getParameterTypeList()) {
            parameterTypeList.add(type.toString());
        }

        String extractedMethod = StringUtils
                .calculateSignatureWithoutClassName(ref.getExtractedOperation().getName(), parameterTypeList);

        if (ref.getRefactoringType() == RefactoringType.EXTRACT_AND_MOVE_OPERATION) {
            info.setGroup(Group.METHOD)
                    .setThreeSided(true)
                    .setDetailsBefore(classNameBefore)
                    .setDetailsAfter(classNameAfter)
                    .setElementBefore(extractedMethod)
                    .setElementAfter(null)
                    .setNameBefore(StringUtils.calculateSignatureForJavaMethod(ref.getSourceOperationBeforeExtraction()))
                    .setNameAfter(StringUtils.calculateSignatureForJavaMethod(ref.getSourceOperationAfterExtraction()))
                    .addMarking(CodeRange.createCodeRangeFromJava(ref.getExtractedCodeRangeFromSourceOperation()),
                            CodeRange.createCodeRangeFromJava(ref.getExtractedCodeRangeToExtractedOperation()),
                            CodeRange.createCodeRangeFromJava(ref.getExtractedCodeRangeFromSourceOperation()),
                            VisualizationType.LEFT,
                            null,
                            RefactoringLine.MarkingOption.NONE,
                            true);

            ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
                    info.addMarking(CodeRange.createCodeRangeFromJava(ref.getSourceOperationCodeRangeBeforeExtraction()),
                            CodeRange.createCodeRangeFromJava(ref.getExtractedOperation().getBody().getCompositeStatement().codeRange()),
                            CodeRange.createCodeRangeFromJava(invocation),
                            VisualizationType.RIGHT,
                            refactoringLine -> refactoringLine.setWord(new String[]{
                                    null,
                                    ref.getExtractedOperation().getName(),
                                    null
                            }),
                            RefactoringLine.MarkingOption.EXTRACT,
                            true));
            return info;
        } else {
            info.setGroup(Group.METHOD)
                    .setDetailsBefore(classNameBefore)
                    .setDetailsAfter(classNameAfter)
                    .setElementBefore(extractedMethod)
                    .setElementAfter(null)
                    .setNameBefore(StringUtils.calculateSignatureForJavaMethod(ref.getSourceOperationBeforeExtraction()))
                    .setNameAfter(StringUtils.calculateSignatureForJavaMethod(ref.getSourceOperationAfterExtraction()))
                    .addMarking(CodeRange.createCodeRangeFromJava(ref.getSourceOperationCodeRangeBeforeExtraction()),
                            CodeRange.createCodeRangeFromJava(ref.getSourceOperationCodeRangeAfterExtraction()),
                            false)
                    .addMarking(CodeRange.createCodeRangeFromJava(ref.getExtractedCodeRangeFromSourceOperation()),
                            CodeRange.createCodeRangeFromJava(ref.getExtractedCodeRangeToExtractedOperation()),
                            true);

            ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
                    info.addMarking(
                            CodeRange.createCodeRangeFromJava(ref.getExtractedCodeRangeFromSourceOperation()),
                            CodeRange.createCodeRangeFromJava(invocation),
                            null,
                            RefactoringLine.MarkingOption.ADD,
                            true)
            );
            return info;
        }
    }

}
