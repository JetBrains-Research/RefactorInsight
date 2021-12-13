package org.jetbrains.research.refactorinsight.java.impl.data.variables;

import gr.uom.java.xmi.diff.InlineVariableRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.java.impl.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.java.impl.data.util.Utils.calculateSignatureForJavaMethod;
import static org.jetbrains.research.refactorinsight.java.impl.data.util.Utils.createCodeRangeFromJava;

public class InlineVariableJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        InlineVariableRefactoring ref = (InlineVariableRefactoring) refactoring;

        return info.setGroup(Group.VARIABLE)
                .setNameBefore(calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(calculateSignatureForJavaMethod(ref.getOperationAfter()))
                .setElementBefore(ref.getVariableDeclaration().getVariableDeclaration().toQualifiedString())
                .setElementAfter(null)
                .addMarking(createCodeRangeFromJava(ref.getVariableDeclaration().codeRange()),
                        createCodeRangeFromJava(ref.getInlinedVariableDeclarationCodeRange()),
                        true);
    }

}
