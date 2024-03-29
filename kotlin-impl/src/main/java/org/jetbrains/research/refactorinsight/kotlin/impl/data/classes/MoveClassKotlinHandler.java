package org.jetbrains.research.refactorinsight.kotlin.impl.data.classes;

import org.jetbrains.research.kotlinrminer.ide.Refactoring;
import org.jetbrains.research.kotlinrminer.ide.diff.refactoring.MoveClassRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.KotlinRefactoringHandler;

import static org.jetbrains.research.refactorinsight.kotlin.impl.data.util.KotlinUtils.createCodeRangeFromKotlin;

public class MoveClassKotlinHandler extends KotlinRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring,
                                   RefactoringInfo info) {
        MoveClassRefactoring ref = (MoveClassRefactoring) refactoring;
        if (ref.getMovedClass().isInterface()) {
            info.setGroup(Group.INTERFACE);
        } else if (ref.getMovedClass().isAbstract()) {
            info.setGroup(Group.ABSTRACT);
        } else {
            info.setGroup(Group.CLASS);
        }

        String packageBefore = ref.getOriginalClass().getPackageName();
        String packageAfter = ref.getMovedClass().getPackageName();

        String fileBefore = ref.getOriginalClass().getSourceFile();
        String fileAfter = ref.getMovedClass().getSourceFile();

        fileBefore = fileBefore.substring(fileBefore.lastIndexOf("/") + 1);
        //class name before
        final String left = fileBefore.substring(0, fileBefore.lastIndexOf("."));

        fileAfter = fileAfter.substring(fileAfter.lastIndexOf("/") + 1);
        //class name after
        final String right = fileAfter.substring(0, fileAfter.lastIndexOf("."));

        String originalClassName = ref.getOriginalClassName();
        String movedClassName = ref.getMovedClassName();
        originalClassName = originalClassName.contains(".")
                ? originalClassName.substring(originalClassName.lastIndexOf(".") + 1) : originalClassName;
        movedClassName = movedClassName.contains(".")
                ? movedClassName.substring(movedClassName.lastIndexOf(".") + 1) : movedClassName;

        info.setNameBefore(ref.getOriginalClassName())
                .setNameAfter(ref.getMovedClassName())
                .setDetailsBefore(ref.getOriginalClass().getPackageName())
                .setDetailsAfter(ref.getMovedClass().getPackageName());

        //check if it is inner class
        if ((!left.equals(originalClassName) && packageBefore.contains(left))
                || (!right.equals(movedClassName) && packageAfter.contains(right))) {
            String finalOriginalClassName = originalClassName;
            String finalMovedClassName = movedClassName;
            return info.addMarking(createCodeRangeFromKotlin(ref.getOriginalClass().codeRange(), info),
                    createCodeRangeFromKotlin(ref.getMovedClass().codeRange(), info),
                    (line) -> line.setWord(new String[]{finalOriginalClassName, null, finalMovedClassName}),
                    RefactoringLine.MarkingOption.COLLAPSE,
                    false);
        }

        return info.addMarking(createCodeRangeFromKotlin(ref.getOriginalClass().codeRange(), info),
                createCodeRangeFromKotlin(ref.getMovedClass().codeRange(), info),
                (line) -> line.setWord(new String[]{packageBefore, null, packageAfter}),
                RefactoringLine.MarkingOption.PACKAGE,
                true);
    }
}
