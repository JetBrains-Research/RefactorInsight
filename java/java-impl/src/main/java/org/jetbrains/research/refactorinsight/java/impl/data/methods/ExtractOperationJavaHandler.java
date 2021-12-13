package org.jetbrains.research.refactorinsight.java.impl.data.methods;

import gr.uom.java.xmi.UMLType;
import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.common.diff.VisualizationType;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;
import org.jetbrains.research.refactorinsight.java.impl.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

import java.util.ArrayList;
import java.util.List;

import static org.jetbrains.research.refactorinsight.java.impl.data.util.Utils.calculateSignatureForJavaMethod;
import static org.jetbrains.research.refactorinsight.java.impl.data.util.Utils.createCodeRangeFromJava;

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

        String extractedMethod = StringUtils
                .calculateSignatureWithoutClassName(ref.getExtractedOperation().getName(), parameterTypeList);

        if (ref.getRefactoringType() == RefactoringType.EXTRACT_AND_MOVE_OPERATION) {
            info.setGroup(Group.METHOD)
                    .setThreeSided(true)
                    .setDetailsBefore(classNameBefore)
                    .setDetailsAfter(classNameAfter)
                    .setElementBefore(extractedMethod)
                    .setElementAfter(null)
                    .setNameBefore(calculateSignatureForJavaMethod(ref.getSourceOperationBeforeExtraction()))
                    .setNameAfter(calculateSignatureForJavaMethod(ref.getSourceOperationAfterExtraction()))
                    .addMarking(createCodeRangeFromJava(ref.getExtractedCodeRangeFromSourceOperation()),
                            createCodeRangeFromJava(ref.getExtractedCodeRangeToExtractedOperation()),
                            createCodeRangeFromJava(ref.getExtractedCodeRangeFromSourceOperation()),
                            VisualizationType.LEFT,
                            null,
                            RefactoringLine.MarkingOption.NONE,
                            true);

            ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
                    info.addMarking(createCodeRangeFromJava(ref.getSourceOperationCodeRangeBeforeExtraction()),
                            createCodeRangeFromJava(ref.getExtractedOperation().getBody().getCompositeStatement().codeRange()),
                            createCodeRangeFromJava(invocation),
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
                    .setNameBefore(calculateSignatureForJavaMethod(ref.getSourceOperationBeforeExtraction()))
                    .setNameAfter(calculateSignatureForJavaMethod(ref.getSourceOperationAfterExtraction()))
                    .addMarking(createCodeRangeFromJava(ref.getSourceOperationCodeRangeBeforeExtraction()),
                            createCodeRangeFromJava(ref.getSourceOperationCodeRangeAfterExtraction()),
                            false)
                    .addMarking(createCodeRangeFromJava(ref.getExtractedCodeRangeFromSourceOperation()),
                            createCodeRangeFromJava(ref.getExtractedCodeRangeToExtractedOperation()),
                            true);

            ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
                    info.addMarking(
                            createCodeRangeFromJava(ref.getExtractedCodeRangeFromSourceOperation()),
                            createCodeRangeFromJava(invocation),
                            null,
                            RefactoringLine.MarkingOption.ADD,
                            true)
            );
            return info;
        }
    }

}
