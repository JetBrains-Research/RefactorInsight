package org.jetbrains.research.refactorinsight.data.variables;

import gr.uom.java.xmi.diff.RemoveVariableModifierRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.RefactoringLine.MarkingOption.NONE;
import static org.jetbrains.research.refactorinsight.data.RefactoringLine.MarkingOption.REMOVE;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createLocationInfoFromJava;

public class RemoveVariableModifierJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        RemoveVariableModifierRefactoring ref = (RemoveVariableModifierRefactoring) refactoring;

        String classNameBefore = ref.getOperationBefore().getName();
        String classNameAfter = ref.getOperationAfter().getName();

        return info.setGroup(Group.VARIABLE)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(ref.getVariableBefore().getVariableDeclaration().toQualifiedString())
                .setNameAfter(ref.getVariableAfter().getVariableDeclaration().toQualifiedString())
                .setElementBefore(ref.getModifier())
                .setElementAfter(null)
                .addMarking(
                        createCodeRangeFromJava(ref.getVariableBefore().codeRange()),
                        createCodeRangeFromJava(ref.getVariableAfter().codeRange()),
                        line -> line.addOffset(createLocationInfoFromJava(ref.getVariableBefore().getLocationInfo()), REMOVE)
                                .setHasColumns(false),
                        NONE,
                        true);
    }
    
}
