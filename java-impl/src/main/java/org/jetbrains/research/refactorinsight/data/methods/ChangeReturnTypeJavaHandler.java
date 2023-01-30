package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.diff.ChangeReturnTypeRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.util.JavaUtils;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

public class ChangeReturnTypeJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ChangeReturnTypeRefactoring ref = (ChangeReturnTypeRefactoring) refactoring;
        if (ref.getOperationAfter().isGetter()
                && !ref.getOperationAfter().getBody().getAllVariables().isEmpty()) {
            String id = ref.getOperationAfter().getClassName() + "."
                    + ref.getOperationAfter().getBody().getAllVariables().get(0);
            info.setGroupId(id);
        }

        String classNameBefore = ref.getOperationBefore().getClassName();
        String classNameAfter = ref.getOperationAfter().getClassName();

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setElementBefore(ref.getOriginalType().toString())
                .setElementAfter(ref.getChangedType().toString())
                .setNameBefore(JavaUtils.calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(JavaUtils.calculateSignatureForJavaMethod(ref.getOperationAfter()))
                .addMarking(JavaUtils.createCodeRangeFromJava(ref.getOriginalType().codeRange()),
                        JavaUtils.createCodeRangeFromJava(ref.getChangedType().codeRange()),
                        true);

    }

}
