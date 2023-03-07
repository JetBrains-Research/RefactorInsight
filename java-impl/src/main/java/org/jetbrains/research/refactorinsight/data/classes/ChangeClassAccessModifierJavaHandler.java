package org.jetbrains.research.refactorinsight.data.classes;

import gr.uom.java.xmi.diff.ChangeClassAccessModifierRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;

public class ChangeClassAccessModifierJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ChangeClassAccessModifierRefactoring ref = (ChangeClassAccessModifierRefactoring) refactoring;

        if (ref.getClassAfter().isAbstract()) {
            info.setGroup(Group.ABSTRACT);
        } else if (ref.getClassAfter().isInterface()) {
            info.setGroup(Group.INTERFACE);
        } else {
            info.setGroup(Group.CLASS);
        }

        return info
                .setDetailsBefore(ref.getClassBefore().getPackageName())
                .setDetailsAfter(ref.getClassAfter().getPackageName())
                .setNameBefore(ref.getClassBefore().getName())
                .setNameAfter(ref.getClassAfter().getName())
                .addMarking(
                        createCodeRangeFromJava(ref.getOldModifier().codeRange()),
                        createCodeRangeFromJava(ref.getNewModifier().codeRange()),
                        true);
    }

}
