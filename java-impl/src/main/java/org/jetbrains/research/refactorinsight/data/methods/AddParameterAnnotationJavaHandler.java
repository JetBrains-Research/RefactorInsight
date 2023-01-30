package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.diff.AddVariableAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.data.util.JavaUtils;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

public class AddParameterAnnotationJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        AddVariableAnnotationRefactoring ref = (AddVariableAnnotationRefactoring) refactoring;

        String classNameBefore = ref.getOperationBefore().getClassName();
        String classNameAfter = ref.getOperationAfter().getClassName();

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(JavaUtils.calculateSignatureForVariableDeclarationContainer(ref.getOperationBefore()))
                .setNameAfter(JavaUtils.calculateSignatureForVariableDeclarationContainer(ref.getOperationAfter()))
                .setElementAfter(null)
                .setElementBefore(ref.getAnnotation().toString() + " added to "
                        + ref.getVariableAfter().getVariableDeclaration().getVariableName())
                .addMarking(JavaUtils.createCodeRangeFromJava(ref.getOperationBefore().codeRange()),
                        JavaUtils.createCodeRangeFromJava(ref.getOperationAfter().codeRange()),
                        line -> line.addOffset(
                                        JavaUtils.createLocationInfoFromJava(ref.getAnnotation().getLocationInfo()),
                                        RefactoringLine.MarkingOption.ADD)
                                .setHasColumns(false),
                        RefactoringLine.MarkingOption.NONE,
                        true);
    }

}
