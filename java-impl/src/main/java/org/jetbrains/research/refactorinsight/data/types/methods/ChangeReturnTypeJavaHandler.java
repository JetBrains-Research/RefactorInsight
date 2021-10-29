package org.jetbrains.research.refactorinsight.data.types.methods;

import gr.uom.java.xmi.diff.ChangeReturnTypeRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.common.adapters.CodeRange.createCodeRangeFromJava;

public class ChangeReturnTypeJavaHandler extends Handler {

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
                .setNameBefore(StringUtils.calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(StringUtils.calculateSignatureForJavaMethod(ref.getOperationAfter()))
                .addMarking(createCodeRangeFromJava(ref.getOriginalType().codeRange()),
                        createCodeRangeFromJava(ref.getChangedType().codeRange()),
                        true);

    }

}
