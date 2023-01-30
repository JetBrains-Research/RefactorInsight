package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.diff.RemoveVariableAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.data.util.JavaUtils;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

public class RemoveParameterAnnotationJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        RemoveVariableAnnotationRefactoring ref = (RemoveVariableAnnotationRefactoring) refactoring;
        String classNameBefore = ref.getOperationBefore().getClassName();
        String classNameAfter = ref.getOperationAfter().getClassName();

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(JavaUtils.calculateSignatureForVariableDeclarationContainer(ref.getOperationBefore()))
                .setNameAfter(JavaUtils.calculateSignatureForVariableDeclarationContainer(ref.getOperationAfter()))
                .setElementAfter(null)
                .setElementBefore(ref.getAnnotation().toString() + " removed from "
                        + ref.getVariableAfter().getVariableDeclaration().getVariableName())
                .addMarking(JavaUtils.createCodeRangeFromJava(ref.getOperationBefore().codeRange()),
                        JavaUtils.createCodeRangeFromJava(ref.getOperationAfter().codeRange()),
                        line -> line.addOffset(
                                JavaUtils.createLocationInfoFromJava(ref.getAnnotation().getLocationInfo()),
                                RefactoringLine.MarkingOption.REMOVE).setHasColumns(false),
                        RefactoringLine.MarkingOption.NONE,
                        true);
    }

}
