package org.jetbrains.research.refactorinsight.java.impl.data.variables;

import gr.uom.java.xmi.diff.RenameVariableRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.java.impl.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.java.impl.data.util.JavaUtils.calculateSignatureForJavaMethod;
import static org.jetbrains.research.refactorinsight.java.impl.data.util.JavaUtils.createCodeRangeFromJava;

public class RenameVariableJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        RenameVariableRefactoring ref = (RenameVariableRefactoring) refactoring;
        String id = ref.getOperationAfter().getClassName() + ".";
        if ((ref.getOperationAfter().isConstructor() || ref.getOperationAfter().isSetter())
                && ref.getRenamedVariable().isParameter()) {
            id += ref.getRenamedVariable().getVariableName();
        } else {
            id = calculateSignatureForJavaMethod(ref.getOperationAfter()) + "."
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

        return info.setElementBefore(ref.getOriginalVariable().getVariableDeclaration().toQualifiedString())
                .setElementAfter(ref.getRenamedVariable().getVariableDeclaration().toQualifiedString())
                .setNameBefore(calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(calculateSignatureForJavaMethod(ref.getOperationAfter()))
                .addMarking(createCodeRangeFromJava(ref.getOriginalVariable().getVariableDeclaration().codeRange()),
                        createCodeRangeFromJava(ref.getRenamedVariable().codeRange()),
                        true);
    }

}
