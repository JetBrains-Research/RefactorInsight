package org.jetbrains.research.refactorinsight.java.impl.data.variables;

import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.VariableDeclarationContainer;
import gr.uom.java.xmi.diff.ChangeVariableTypeRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.java.api.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.java.api.util.Utils.calculateSignatureForJavaMethod;
import static org.jetbrains.research.refactorinsight.java.api.util.Utils.createCodeRangeFromJava;

public class ChangeVariableTypeJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ChangeVariableTypeRefactoring ref = (ChangeVariableTypeRefactoring) refactoring;
        final UMLOperation operationAfter = ref.getOperationAfter();
        String id = operationAfter.getClassName() + ".";

        if ((operationAfter.isSetter() || operationAfter.isConstructor())
                && ref.getChangedTypeVariable().isParameter()) {
            id += ref.getChangedTypeVariable().getVariableName();
        } else {
            id = calculateSignatureForJavaMethod(ref.getOperationAfter()) + "."
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

        return info.setNameBefore(calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(calculateSignatureForJavaMethod(ref.getOperationAfter()))
                .setElementBefore(ref.getOriginalVariable().getVariableDeclaration().toQualifiedString())
                .setElementAfter(ref.getChangedTypeVariable().getVariableDeclaration().toQualifiedString())
                .addMarking(createCodeRangeFromJava(ref.getOriginalVariable().getType().codeRange()),
                        createCodeRangeFromJava(ref.getChangedTypeVariable().getType().codeRange()),
                        true);
    }

}
