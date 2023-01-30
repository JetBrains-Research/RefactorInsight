package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.diff.ModifyMethodAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.util.JavaUtils;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

public class ModifyMethodAnnotationJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ModifyMethodAnnotationRefactoring ref = (ModifyMethodAnnotationRefactoring) refactoring;

        String classNameBefore = ref.getOperationBefore().getClassName();
        String classNameAfter = ref.getOperationAfter().getClassName();

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setElementBefore(ref.getAnnotationBefore().toString())
                .setElementAfter(ref.getAnnotationAfter().toString())
                .addMarking(JavaUtils.createCodeRangeFromJava(ref.getAnnotationBefore().codeRange()),
                        JavaUtils.createCodeRangeFromJava(ref.getAnnotationAfter().codeRange()),
                        true)
                .setNameBefore(JavaUtils.calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(JavaUtils.calculateSignatureForJavaMethod(ref.getOperationAfter()));
    }

}
