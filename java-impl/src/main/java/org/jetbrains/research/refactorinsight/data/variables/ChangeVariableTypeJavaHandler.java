package org.jetbrains.research.refactorinsight.data.variables;

import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.diff.ChangeVariableTypeRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.util.JavaUtils;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;

public class ChangeVariableTypeJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ChangeVariableTypeRefactoring ref = (ChangeVariableTypeRefactoring) refactoring;
        final UMLOperation operationAfter = (UMLOperation) ref.getOperationAfter();
        String id = operationAfter.getClassName() + ".";
        if ((operationAfter.isSetter() || operationAfter.isConstructor())
                && ref.getChangedTypeVariable().isParameter()) {
            id += ref.getChangedTypeVariable().getVariableName();
        } else {
            id = JavaUtils.calculateSignatureForVariableDeclarationContainer(ref.getOperationAfter()) + "."
                    + ref.getChangedTypeVariable().getVariableName();
        }
        info.setGroupId(id);
        if (ref.getChangedTypeVariable().isParameter()) {
            info.setGroup(Group.METHOD)
                    .setDetailsBefore(ref.getOperationBefore().getClassName())
                    .setDetailsAfter(ref.getOperationAfter().getClassName());
        } else {
            info.setGroup(Group.VARIABLE);
        }

        return info
                .setNameBefore(JavaUtils.calculateSignatureForVariableDeclarationContainer(ref.getOperationBefore()))
                .setNameAfter(JavaUtils.calculateSignatureForVariableDeclarationContainer(ref.getOperationAfter()))
                .setElementBefore(ref.getOriginalVariable().getVariableDeclaration().toQualifiedString())
                .setElementAfter(ref.getChangedTypeVariable().getVariableDeclaration().toQualifiedString())
                .addMarking(createCodeRangeFromJava(ref.getOriginalVariable().getVariableDeclaration().codeRange()),
                        createCodeRangeFromJava(ref.getChangedTypeVariable().getVariableDeclaration().codeRange()),
                        true);

    }

}
