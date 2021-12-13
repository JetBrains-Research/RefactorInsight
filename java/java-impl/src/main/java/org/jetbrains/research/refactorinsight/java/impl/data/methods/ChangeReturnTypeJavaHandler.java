package org.jetbrains.research.refactorinsight.java.impl.data.methods;

import gr.uom.java.xmi.diff.ChangeReturnTypeRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.java.impl.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.java.impl.data.util.Utils.calculateSignatureForJavaMethod;
import static org.jetbrains.research.refactorinsight.java.impl.data.util.Utils.createCodeRangeFromJava;

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
                .setNameBefore(calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(calculateSignatureForJavaMethod(ref.getOperationAfter()))
                .addMarking(createCodeRangeFromJava(ref.getOriginalType().codeRange()),
                        createCodeRangeFromJava(ref.getChangedType().codeRange()),
                        true);

    }

}
