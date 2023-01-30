package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.AddMethodAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.data.util.JavaUtils;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

public class AddMethodAnnotationJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        AddMethodAnnotationRefactoring ref = (AddMethodAnnotationRefactoring) refactoring;
        UMLAnnotation annotation = ref.getAnnotation();

        String classNameBefore = ref.getOperationBefore().getClassName();
        String classNameAfter = ref.getOperationAfter().getClassName();

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setElementBefore(annotation.toString())
                .setElementAfter(null)
                .addMarking(JavaUtils.createCodeRangeFromJava(ref.getOperationBefore().codeRange()),
                        JavaUtils.createCodeRangeFromJava(annotation.codeRange()),
                        line -> line.addOffset(
                                JavaUtils.createLocationInfoFromJava(annotation.getLocationInfo()),
                                RefactoringLine.MarkingOption.ADD),
                        RefactoringLine.MarkingOption.ADD,
                        false)
                .setNameBefore(JavaUtils.calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(JavaUtils.calculateSignatureForJavaMethod(ref.getOperationAfter()));
    }

}
