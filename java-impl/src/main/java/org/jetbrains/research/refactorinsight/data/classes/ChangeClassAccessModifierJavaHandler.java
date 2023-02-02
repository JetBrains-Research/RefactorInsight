package org.jetbrains.research.refactorinsight.data.classes;

import gr.uom.java.xmi.diff.ChangeClassAccessModifierRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createLocationInfoFromJava;

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
                        createCodeRangeFromJava(ref.getClassBefore().codeRange()),
                        createCodeRangeFromJava(ref.getClassAfter().codeRange()),
                        line -> line.addOffset(
                                createLocationInfoFromJava(ref.getClassAfter().getLocationInfo()), RefactoringLine.MarkingOption.ADD),
                        RefactoringLine.MarkingOption.ADD,
                        false);
    }

}
