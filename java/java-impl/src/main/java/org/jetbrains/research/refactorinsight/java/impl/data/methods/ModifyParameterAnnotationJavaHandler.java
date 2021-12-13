package org.jetbrains.research.refactorinsight.java.impl.data.methods;

import gr.uom.java.xmi.diff.ModifyVariableAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.java.impl.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.java.impl.data.util.Utils.calculateSignatureForJavaMethod;
import static org.jetbrains.research.refactorinsight.java.impl.data.util.Utils.createCodeRangeFromJava;

public class ModifyParameterAnnotationJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ModifyVariableAnnotationRefactoring ref = (ModifyVariableAnnotationRefactoring) refactoring;
        String classNameBefore = ref.getOperationBefore().getClassName();
        String classNameAfter = ref.getOperationAfter().getClassName();

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(calculateSignatureForJavaMethod(ref.getOperationAfter()))
                .setElementAfter(ref.getAnnotationAfter().toString() + " for parameter "
                        + ref.getVariableAfter().getVariableName())
                .setElementBefore(ref.getAnnotationBefore().toString())
                .addMarking(createCodeRangeFromJava(ref.getAnnotationBefore().codeRange()),
                        createCodeRangeFromJava(ref.getAnnotationAfter().codeRange()),
                        true);
    }

}
