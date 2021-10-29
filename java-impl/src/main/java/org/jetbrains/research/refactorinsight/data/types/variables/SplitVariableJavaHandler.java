package org.jetbrains.research.refactorinsight.data.types.variables;

import gr.uom.java.xmi.decomposition.VariableDeclaration;
import gr.uom.java.xmi.diff.SplitVariableRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

import java.util.stream.Collectors;

public class SplitVariableJavaHandler extends Handler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        SplitVariableRefactoring ref = (SplitVariableRefactoring) refactoring;

        ref.getSplitVariables().forEach(var ->
                info.addMarking(CodeRange.createCodeRangeFromJava(ref.getOldVariable().codeRange()),
                        CodeRange.createCodeRangeFromJava(var.codeRange()),
                        true));

        if (ref.getOldVariable().isParameter()) {
            info.setGroup(Group.METHOD)
                    .setDetailsBefore(ref.getOperationBefore().getClassName())
                    .setDetailsAfter(ref.getOperationAfter().getClassName());
        } else {
            info.setGroup(Group.VARIABLE);
        }

        return info.setNameBefore(StringUtils.calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(StringUtils.calculateSignatureForJavaMethod(ref.getOperationAfter()))
                .setElementBefore(ref.getOldVariable().getVariableDeclaration().toQualifiedString())
                .setElementAfter(ref.getSplitVariables().stream()
                        .map(VariableDeclaration::getVariableName)
                        .collect(Collectors.joining()));
    }

}
