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
        String classNameBefore = ref.getSourceOperationBeforeExtraction().getClassName();
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
                    .setThreeSided(true)
                    .setDetailsBefore(classNameBefore)
                    .setDetailsAfter(classNameAfter)
                    .setElementBefore(extractedMethod)
                    .setElementAfter(null)
                    .setNameBefore(JavaUtils.calculateSignatureForVariableDeclarationContainer(ref.getSourceOperationBeforeExtraction()))
                    .setNameAfter(JavaUtils.calculateSignatureForVariableDeclarationContainer(ref.getSourceOperationAfterExtraction()))
                    .addMarking(JavaUtils.createCodeRangeFromJava(ref.getExtractedCodeRangeFromSourceOperation()),
                            JavaUtils.createCodeRangeFromJava(ref.getExtractedCodeRangeToExtractedOperation()),
                            JavaUtils.createCodeRangeFromJava(ref.getExtractedCodeRangeFromSourceOperation()),
                            VisualizationType.LEFT,
                            null,
                            RefactoringLine.MarkingOption.NONE,
                            true);

            ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
                    info.addMarking(JavaUtils.createCodeRangeFromJava(ref.getSourceOperationCodeRangeBeforeExtraction()),
                            JavaUtils.createCodeRangeFromJava(ref.getExtractedOperation().getBody().getCompositeStatement().codeRange()),
                            JavaUtils.createCodeRangeFromJava(invocation),
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
                    .setNameBefore(JavaUtils.calculateSignatureForVariableDeclarationContainer(ref.getSourceOperationBeforeExtraction()))
                    .setNameAfter(JavaUtils.calculateSignatureForVariableDeclarationContainer(ref.getSourceOperationAfterExtraction()))
                    .addMarking(JavaUtils.createCodeRangeFromJava(ref.getSourceOperationCodeRangeBeforeExtraction()),
                            JavaUtils.createCodeRangeFromJava(ref.getSourceOperationCodeRangeAfterExtraction()),
                            false)
                    .addMarking(JavaUtils.createCodeRangeFromJava(ref.getExtractedCodeRangeFromSourceOperation()),
                            JavaUtils.createCodeRangeFromJava(ref.getExtractedCodeRangeToExtractedOperation()),
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
