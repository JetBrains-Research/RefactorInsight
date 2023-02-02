package org.jetbrains.research.refactorinsight.data.classes;

import gr.uom.java.xmi.diff.RemoveClassModifierRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;
import static org.jetbrains.research.refactorinsight.data.RefactoringLine.MarkingOption.ADD;
import static org.jetbrains.research.refactorinsight.data.RefactoringLine.MarkingOption.NONE;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createLocationInfoFromJava;

public class RemoveClassModifierJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        RemoveClassModifierRefactoring ref = (RemoveClassModifierRefactoring) refactoring;

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
                .setElementBefore(ref.getModifier())
                .setElementAfter(null)
                .addMarking(
                        createCodeRangeFromJava(ref.getClassBefore().codeRange()),
                        createCodeRangeFromJava(ref.getClassAfter().codeRange()),
                        line -> line.addOffset(
                                        createLocationInfoFromJava(ref.getClassBefore().getLocationInfo()), ADD)
                                .setHasColumns(false),
                        NONE,
                        true);
    }
    
}
