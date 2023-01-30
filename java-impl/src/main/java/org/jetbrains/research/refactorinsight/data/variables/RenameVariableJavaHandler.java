package org.jetbrains.research.refactorinsight.data.variables;

import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.diff.RenameVariableRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.calculateSignatureForVariableDeclarationContainer;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;

public class RenameVariableJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        RenameVariableRefactoring ref = (RenameVariableRefactoring) refactoring;
        String id = ref.getOperationAfter().getClassName() + ".";
        if ((ref.getOperationAfter().isConstructor() || ((UMLOperation) ref.getOperationAfter()).isSetter())
                && ref.getRenamedVariable().isParameter()) {
            id += ref.getRenamedVariable().getVariableName();
        } else {
            id = calculateSignatureForVariableDeclarationContainer(ref.getOperationAfter()) + "."
                    + ref.getRenamedVariable().getVariableName();
        }
        info.setGroupId(id);

        if (ref.getRenamedVariable().isParameter()) {
            info.setGroup(Group.METHOD)
                    .setDetailsBefore(ref.getOperationBefore().getClassName())
                    .setDetailsAfter(ref.getOperationAfter().getClassName());
        } else {
            info.setGroup(Group.VARIABLE);
        }

        return info
                .setElementBefore(ref.getOriginalVariable().getVariableDeclaration().toQualifiedString())
                .setElementAfter(ref.getRenamedVariable().getVariableDeclaration().toQualifiedString())
                .setNameBefore(calculateSignatureForVariableDeclarationContainer(ref.getOperationBefore()))
                .setNameAfter(calculateSignatureForVariableDeclarationContainer(ref.getOperationAfter()))
                .addMarking(createCodeRangeFromJava(ref.getOriginalVariable().getVariableDeclaration().codeRange()),
                        createCodeRangeFromJava(ref.getRenamedVariable().codeRange()), true);
    }

}
