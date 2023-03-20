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

        if (ref.getSubExpressionMappings().isEmpty()) {
            ref.getReferences().forEach(reference ->
                    info.addMarking(
                            createCodeRangeFromJava(ref.getVariableDeclaration().codeRange()),
                            createCodeRangeFromJava(reference.getFragment2().codeRange()),
                            true
                    )
            );
        } else {
            ref.getSubExpressionMappings().forEach(leafMapping ->
                    info.addMarking(
                            createCodeRangeFromJava(leafMapping.getFragment1().codeRange()),
                            createCodeRangeFromJava(leafMapping.getFragment2().codeRange()),
                            true
                    )
            );
        }

        return info.setGroup(Group.VARIABLE)
                .setNameBefore(calculateSignatureForVariableDeclarationContainer(ref.getOperationBefore()))
                .setNameAfter(calculateSignatureForVariableDeclarationContainer(ref.getOperationAfter()))
                .setElementBefore(ref.getVariableDeclaration().getVariableDeclaration().toQualifiedString())
                .setElementAfter(null);
    }

}
