package org.jetbrains.research.refactorinsight.java.impl.data.variables;

import gr.uom.java.xmi.decomposition.VariableDeclaration;
import gr.uom.java.xmi.diff.MergeVariableRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.java.impl.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import java.util.stream.Collectors;

import static org.jetbrains.research.refactorinsight.java.impl.data.util.Utils.calculateSignatureForJavaMethod;
import static org.jetbrains.research.refactorinsight.java.impl.data.util.Utils.createCodeRangeFromJava;

public class MergeVariableJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        MergeVariableRefactoring ref = (MergeVariableRefactoring) refactoring;

        ref.getMergedVariables().forEach(var ->
                info.addMarking(createCodeRangeFromJava(var.codeRange()),
                        createCodeRangeFromJava(ref.getNewVariable().codeRange()),
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
                .setNameBefore(calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(calculateSignatureForJavaMethod(ref.getOperationAfter()));
    }

}
