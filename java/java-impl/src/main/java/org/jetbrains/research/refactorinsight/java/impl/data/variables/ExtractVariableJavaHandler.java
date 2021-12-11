package org.jetbrains.research.refactorinsight.java.impl.data.variables;

import gr.uom.java.xmi.diff.ExtractVariableRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.java.api.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.java.api.util.Utils.calculateSignatureForJavaMethod;
import static org.jetbrains.research.refactorinsight.java.api.util.Utils.createCodeRangeFromJava;

public class ExtractVariableJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ExtractVariableRefactoring ref = (ExtractVariableRefactoring) refactoring;

        return info.setGroup(Group.VARIABLE)
                .setNameBefore(calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(calculateSignatureForJavaMethod(ref.getOperationAfter()))
                .setElementBefore(ref.getVariableDeclaration().getVariableDeclaration().toQualifiedString())
                .setElementAfter(null)
                .addMarking(createCodeRangeFromJava(ref.getOperationBefore().getBody().getCompositeStatement().codeRange()),
                        createCodeRangeFromJava(ref.getExtractedVariableDeclarationCodeRange()), true);
    }

}
