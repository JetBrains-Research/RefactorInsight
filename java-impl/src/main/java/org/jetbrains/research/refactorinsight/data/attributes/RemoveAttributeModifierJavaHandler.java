package org.jetbrains.research.refactorinsight.data.attributes;

import gr.uom.java.xmi.diff.RemoveAttributeModifierRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.RefactoringLine.MarkingOption.NONE;
import static org.jetbrains.research.refactorinsight.data.RefactoringLine.MarkingOption.REMOVE;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createLocationInfoFromJava;

public class RemoveAttributeModifierJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        RemoveAttributeModifierRefactoring ref = (RemoveAttributeModifierRefactoring) refactoring;

        String classNameBefore = ref.getAttributeBefore().getClassName();
        String classNameAfter = ref.getAttributeAfter().getClassName();

        return info.setGroup(Group.ATTRIBUTE)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(ref.getAttributeBefore().getVariableDeclaration().toQualifiedString())
                .setNameAfter(ref.getAttributeAfter().getVariableDeclaration().toQualifiedString())
                .setElementBefore(ref.getModifier())
                .setElementAfter(null)
                .addMarking(
                        createCodeRangeFromJava(ref.getAttributeBefore().codeRange()),
                        createCodeRangeFromJava(ref.getAttributeAfter().codeRange()),
                        line -> line.addOffset(createLocationInfoFromJava(ref.getAttributeBefore().getLocationInfo()), REMOVE)
                                .setHasColumns(false),
                        NONE,
                        true);
    }

}
