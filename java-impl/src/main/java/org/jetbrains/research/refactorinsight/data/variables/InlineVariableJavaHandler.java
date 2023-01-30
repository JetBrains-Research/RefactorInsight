package org.jetbrains.research.refactorinsight.data.variables;

import gr.uom.java.xmi.diff.InlineVariableRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.*;

public class InlineVariableJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        InlineVariableRefactoring ref = (InlineVariableRefactoring) refactoring;

        return info.setGroup(Group.VARIABLE)
                .setNameBefore(calculateSignatureForVariableDeclarationContainer(ref.getOperationBefore()))
                .setNameAfter(calculateSignatureForVariableDeclarationContainer(ref.getOperationAfter()))
                .setElementBefore(ref.getVariableDeclaration().getVariableDeclaration().toQualifiedString())
                .setElementAfter(null)
                .addMarking(createCodeRangeFromJava(ref.getVariableDeclaration().codeRange()),
                        createCodeRangeFromJava(ref.getInlinedVariableDeclarationCodeRange()),
                        true);
    }

}