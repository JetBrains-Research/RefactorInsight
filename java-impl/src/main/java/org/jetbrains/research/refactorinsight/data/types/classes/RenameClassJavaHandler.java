package org.jetbrains.research.refactorinsight.data.types.classes;

import gr.uom.java.xmi.diff.RenameClassRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.refactoringminer.api.Refactoring;

public class RenameClassJavaHandler extends Handler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        RenameClassRefactoring ref = (RenameClassRefactoring) refactoring;

        if (ref.getRenamedClass().isAbstract()) {
            info.setGroup(Group.ABSTRACT);
        } else if (ref.getRenamedClass().isInterface()) {
            info.setGroup(Group.INTERFACE);
        } else {
            info.setGroup(Group.CLASS);
        }

        String[] nameSpaceBefore = ref.getOriginalClassName().split("\\.");
        String classNameBefore = nameSpaceBefore[nameSpaceBefore.length - 1];
        String[] nameSpaceAfter = ref.getRenamedClass().getName().split("\\.");
        String classNameAfter = nameSpaceAfter[nameSpaceAfter.length - 1];

        return info.addMarking(CodeRange.createCodeRangeFromJava(ref.getOriginalClass().codeRange()),
                        CodeRange.createCodeRangeFromJava(ref.getRenamedClass().codeRange()),
                        line -> line.setWord(new String[]{classNameBefore, null, classNameAfter}),
                        RefactoringLine.MarkingOption.COLLAPSE,
                        true)
                .setNameBefore(ref.getOriginalClassName())
                .setNameAfter(ref.getRenamedClassName())
                .setDetailsBefore(ref.getOriginalClass().getPackageName())
                .setDetailsAfter(ref.getRenamedClass().getPackageName())
                .setElementBefore(null)
                .setElementAfter(null);
    }

}
