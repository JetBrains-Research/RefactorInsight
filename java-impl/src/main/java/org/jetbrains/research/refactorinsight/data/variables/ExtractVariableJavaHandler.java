package org.jetbrains.research.refactorinsight.data.variables;

import gr.uom.java.xmi.diff.ExtractVariableRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.calculateSignatureForVariableDeclarationContainer;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;

public class ExtractVariableJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ExtractVariableRefactoring ref = (ExtractVariableRefactoring) refactoring;

        return info.setGroup(Group.VARIABLE)
                .setNameBefore(calculateSignatureForVariableDeclarationContainer(ref.getOperationBefore()))
                .setNameAfter(calculateSignatureForVariableDeclarationContainer(ref.getOperationAfter()))
                .setElementBefore(ref.getVariableDeclaration().getVariableDeclaration().toQualifiedString())
                .setElementAfter(null)
                .addMarking(createCodeRangeFromJava(ref.getOperationBefore().getBody().getCompositeStatement().codeRange()),
                        createCodeRangeFromJava(ref.getExtractedVariableDeclarationCodeRange()), true);
    }


}
