package org.jetbrains.research.refactorinsight.java.impl.data.variables;

import gr.uom.java.xmi.decomposition.VariableDeclaration;
import gr.uom.java.xmi.diff.SplitVariableRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.java.impl.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import java.util.stream.Collectors;

import static org.jetbrains.research.refactorinsight.java.impl.data.util.Utils.calculateSignatureForJavaMethod;
import static org.jetbrains.research.refactorinsight.java.impl.data.util.Utils.createCodeRangeFromJava;

public class SplitVariableJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        SplitVariableRefactoring ref = (SplitVariableRefactoring) refactoring;

        ref.getSplitVariables().forEach(var ->
                info.addMarking(createCodeRangeFromJava(ref.getOldVariable().codeRange()),
                        createCodeRangeFromJava(var.codeRange()),
                        true));

        if (ref.getOldVariable().isParameter()) {
            info.setGroup(Group.METHOD)
                    .setDetailsBefore(ref.getOperationBefore().getClassName())
                    .setDetailsAfter(ref.getOperationAfter().getClassName());
        } else {
            info.setGroup(Group.VARIABLE);
        }

        return info.setNameBefore(calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(calculateSignatureForJavaMethod(ref.getOperationAfter()))
                .setElementBefore(ref.getOldVariable().getVariableDeclaration().toQualifiedString())
                .setElementAfter(ref.getSplitVariables().stream()
                        .map(VariableDeclaration::getVariableName)
                        .collect(Collectors.joining()));
    }

}
