package org.jetbrains.research.refactorinsight.data.attributes;

import gr.uom.java.xmi.diff.ChangeAttributeAccessModifierRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;

public class ChangeAttributeAccessModifierJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ChangeAttributeAccessModifierRefactoring ref = (ChangeAttributeAccessModifierRefactoring) refactoring;

        String classNameBefore = ref.getAttributeBefore().getClassName();
        String classNameAfter = ref.getAttributeAfter().getClassName();

        return info.setGroup(Group.ATTRIBUTE)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(ref.getAttributeBefore().getVariableDeclaration().toQualifiedString())
                .setNameAfter(ref.getAttributeAfter().getVariableDeclaration().toQualifiedString())
                .addMarking(createCodeRangeFromJava(ref.getOldModifier().codeRange()),
                        createCodeRangeFromJava(ref.getNewModifier().codeRange()),
                        true);
    }

}
