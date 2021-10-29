package org.jetbrains.research.refactorinsight.data.types.variables;

import gr.uom.java.xmi.diff.RenameVariableRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

public class RenameVariableJavaHandler extends Handler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        RenameVariableRefactoring ref = (RenameVariableRefactoring) refactoring;
        String id = ref.getOperationAfter().getClassName() + ".";
        if ((ref.getOperationAfter().isConstructor() || ref.getOperationAfter().isSetter())
                && ref.getRenamedVariable().isParameter()) {
            id += ref.getRenamedVariable().getVariableName();
        } else {
            id = StringUtils.calculateSignatureForJavaMethod(ref.getOperationAfter()) + "."
                    + ref.getRenamedVariable().getVariableName();
        }
        info.setGroupId(id);

        if (ref.getRenamedVariable().isParameter()) {
            info.setGroup(Group.METHOD)
                    .setDetailsBefore(ref.getOperationBefore().getClassName())
                    .setDetailsAfter(ref.getOperationAfter().getClassName());
        } else {
            info.setGroup(Group.VARIABLE);
        }

        return info.setElementBefore(ref.getOriginalVariable().getVariableDeclaration().toQualifiedString())
                .setElementAfter(ref.getRenamedVariable().getVariableDeclaration().toQualifiedString())
                .setNameBefore(StringUtils.calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(StringUtils.calculateSignatureForJavaMethod(ref.getOperationAfter()))
                .addMarking(CodeRange.createCodeRangeFromJava(ref.getOriginalVariable().getVariableDeclaration().codeRange()),
                        CodeRange.createCodeRangeFromJava(ref.getRenamedVariable().codeRange()),
                        true);
    }

}
