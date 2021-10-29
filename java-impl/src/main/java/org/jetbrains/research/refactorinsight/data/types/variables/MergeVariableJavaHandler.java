package org.jetbrains.research.refactorinsight.data.types.variables;

import gr.uom.java.xmi.decomposition.VariableDeclaration;
import gr.uom.java.xmi.diff.MergeVariableRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

import java.util.stream.Collectors;

public class MergeVariableJavaHandler extends Handler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        MergeVariableRefactoring ref = (MergeVariableRefactoring) refactoring;

        ref.getMergedVariables().forEach(var ->
                info.addMarking(CodeRange.createCodeRangeFromJava(var.codeRange()),
                        CodeRange.createCodeRangeFromJava(ref.getNewVariable().codeRange()),
                        true));

        if (ref.getNewVariable().isParameter()) {
            info.setGroup(Group.METHOD)
                    .setDetailsBefore(ref.getOperationBefore().getClassName())
                    .setDetailsAfter(ref.getOperationAfter().getClassName());
        } else {
            info.setGroup(Group.VARIABLE);
        }

        return info.setElementBefore(ref.getMergedVariables().stream().map(
                        VariableDeclaration::getVariableName).collect(Collectors.joining()))
                .setElementAfter(ref.getNewVariable().getVariableDeclaration().toQualifiedString())
                .setNameBefore(StringUtils.calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(StringUtils.calculateSignatureForJavaMethod(ref.getOperationAfter()));
    }

}
