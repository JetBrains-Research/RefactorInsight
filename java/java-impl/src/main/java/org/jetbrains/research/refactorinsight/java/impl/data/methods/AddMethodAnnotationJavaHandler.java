package org.jetbrains.research.refactorinsight.java.impl.data.methods;

import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.AddMethodAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.java.impl.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.java.impl.data.util.Utils.*;

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
                .addMarking(createCodeRangeFromJava(ref.getOperationBefore().codeRange()),
                        createCodeRangeFromJava(annotation.codeRange()),
                        line -> line.addOffset(
                                createLocationInfoFromJava(annotation.getLocationInfo()),
                                RefactoringLine.MarkingOption.ADD),
                        RefactoringLine.MarkingOption.ADD,
                        false)
                .setNameBefore(calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(calculateSignatureForJavaMethod(ref.getOperationAfter()));
    }

}
