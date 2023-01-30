package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.diff.ModifyVariableAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.util.JavaUtils;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

public class ModifyParameterAnnotationJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ModifyVariableAnnotationRefactoring ref = (ModifyVariableAnnotationRefactoring) refactoring;
        String classNameBefore = ref.getOperationBefore().getClassName();
        String classNameAfter = ref.getOperationAfter().getClassName();

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(JavaUtils.calculateSignatureForVariableDeclarationContainer(ref.getOperationBefore()))
                .setNameAfter(JavaUtils.calculateSignatureForVariableDeclarationContainer(ref.getOperationAfter()))
                .setElementAfter(ref.getAnnotationAfter().toString() + " for parameter "
                        + ref.getVariableAfter().getVariableName())
                .setElementBefore(ref.getAnnotationBefore().toString())
                .addMarking(JavaUtils.createCodeRangeFromJava(ref.getAnnotationBefore().codeRange()),
                        JavaUtils.createCodeRangeFromJava(ref.getAnnotationAfter().codeRange()),
                        true);
    }

}
