package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.UMLType;
import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import org.jetbrains.research.refactorinsight.data.*;
import org.jetbrains.research.refactorinsight.diff.VisualizationType;
import org.jetbrains.research.refactorinsight.utils.StringUtils;
import org.jetbrains.research.refactorinsight.data.util.JavaUtils;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

import java.util.ArrayList;
import java.util.List;

public class ExtractOperationJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ExtractOperationRefactoring ref = (ExtractOperationRefactoring) refactoring;
        String classNameBefore = ref.getSourceOperationAfterExtraction().getClassName();
        String classNameAfter = ref.getExtractedOperation().getClassName();

        List<String> parameterTypeList = new ArrayList<>();
        for (UMLType type : ref.getExtractedOperation().getParameterTypeList()) {
            parameterTypeList.add(type.toString());
        }

        info.setFoldingDescriptorMid(FoldingBuilder.fromMethod(ref.getExtractedOperation()));

        String extractedMethod = StringUtils
                .calculateSignatureWithoutClassName(ref.getExtractedOperation().getName(), parameterTypeList);

        if (ref.getRefactoringType() == RefactoringType.EXTRACT_AND_MOVE_OPERATION) {
            info.setGroup(Group.METHOD)
                    .setDetailsBefore(classNameBefore)
                    .setDetailsAfter(classNameAfter)
                    .setElementBefore(ref.getSourceOperationAfterExtraction().getName())
                    .setElementAfter(extractedMethod)
                    .setNameBefore(JavaUtils.calculateSignatureForVariableDeclarationContainer(ref.getSourceOperationAfterExtraction()))
                    .setNameAfter(JavaUtils.calculateSignatureForVariableDeclarationContainer(ref.getExtractedOperation()))
                    .addMarking(JavaUtils.createCodeRangeFromJava(ref.getSourceOperationAfterExtraction().codeRange()),
                            JavaUtils.createCodeRangeFromJava(ref.getExtractedOperationCodeRange()),
                            null,
                            RefactoringLine.MarkingOption.ADD,
                            true);

            ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
                    info.addMarking(
                            JavaUtils.createCodeRangeFromJava(invocation),
                            JavaUtils.createCodeRangeFromJava(ref.getExtractedOperationCodeRange()),
                            null,
                            RefactoringLine.MarkingOption.ADD,
                            true)
            );
            return info;
        } else {
            info.setGroup(Group.METHOD)
                    .setDetailsBefore(classNameBefore)
                    .setDetailsAfter(classNameAfter)
                    .setElementBefore(extractedMethod)
                    .setElementAfter(null)
                    .setNameBefore(JavaUtils.calculateSignatureForVariableDeclarationContainer(ref.getSourceOperationBeforeExtraction()))
                    .setNameAfter(JavaUtils.calculateSignatureForVariableDeclarationContainer(ref.getSourceOperationAfterExtraction()))
                    .addMarking(JavaUtils.createCodeRangeFromJava(ref.getSourceOperationCodeRangeBeforeExtraction()),
                            JavaUtils.createCodeRangeFromJava(ref.getSourceOperationCodeRangeAfterExtraction()),
                            false)
                    .addMarking(JavaUtils.createCodeRangeFromJava(ref.getExtractedCodeRangeFromSourceOperation()),
                            JavaUtils.createCodeRangeFromJava(ref.getExtractedOperationCodeRange()),
                            null,
                            RefactoringLine.MarkingOption.ADD,
                            true);

            ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
                    info.addMarking(
                            JavaUtils.createCodeRangeFromJava(ref.getExtractedCodeRangeFromSourceOperation()),
                            JavaUtils.createCodeRangeFromJava(invocation),
                            null,
                            RefactoringLine.MarkingOption.ADD,
                            true)
            );
            return info;
        }
    }

}
