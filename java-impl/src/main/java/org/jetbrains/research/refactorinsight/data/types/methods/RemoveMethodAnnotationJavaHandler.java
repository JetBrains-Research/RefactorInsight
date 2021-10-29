package org.jetbrains.research.refactorinsight.data.types.methods;

import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.RemoveMethodAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.adapters.LocationInfo;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

public class RemoveMethodAnnotationJavaHandler extends Handler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        RemoveMethodAnnotationRefactoring ref = (RemoveMethodAnnotationRefactoring) refactoring;
        UMLAnnotation annotation = ref.getAnnotation();

        String classNameBefore = ref.getOperationBefore().getClassName();
        String classNameAfter = ref.getOperationAfter().getClassName();

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setElementBefore(ref.getAnnotation().toString())
                .setElementAfter(null)
                .addMarking(CodeRange.createCodeRangeFromJava(annotation.codeRange()),
                        CodeRange.createCodeRangeFromJava(ref.getOperationAfter().codeRange()),
                        line -> line.addOffset(LocationInfo.createLocationInfoFromJava(annotation.getLocationInfo()),
                                RefactoringLine.MarkingOption.REMOVE),
                        RefactoringLine.MarkingOption.REMOVE,
                        false)
                .setNameBefore(StringUtils.calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(StringUtils.calculateSignatureForJavaMethod(ref.getOperationAfter()));
    }

}
