package org.jetbrains.research.refactorinsight.kotlin.impl.data.classes;

import org.jetbrains.research.kotlinrminer.api.Refactoring;
import org.jetbrains.research.kotlinrminer.diff.refactoring.MoveAndRenameClassRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.KotlinRefactoringHandler;

import static org.jetbrains.research.refactorinsight.kotlin.impl.data.util.Utils.createCodeRangeFromKotlin;

public class MoveRenameClassKotlinHandler extends KotlinRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring,
                                   RefactoringInfo info) {
        MoveAndRenameClassRefactoring ref = (MoveAndRenameClassRefactoring) refactoring;

        if (ref.getRenamedClass().isAbstract()) {
            info.setGroup(Group.ABSTRACT);
        } else if (ref.getRenamedClass().isInterface()) {
            info.setGroup(Group.INTERFACE);
        } else {
            info.setGroup(Group.CLASS);
        }

        String[] nameSpaceBefore = ref.getOriginalClass().getName().split("\\.");
        String classNameBefore = nameSpaceBefore[nameSpaceBefore.length - 1];
        String[] nameSpaceAfter = ref.getRenamedClass().getName().split("\\.");
        String classNameAfter = nameSpaceAfter[nameSpaceAfter.length - 1];

        info.addMarking(createCodeRangeFromKotlin(ref.getOriginalClass().codeRange()),
                        createCodeRangeFromKotlin(ref.getRenamedClass().codeRange()),
                        (line) -> line.setWord(new String[]{classNameBefore, null, classNameAfter}),
                        RefactoringLine.MarkingOption.COLLAPSE,
                        false)
                .setNameBefore(ref.getOriginalClassName())
                .setNameAfter(ref.getRenamedClassName())
                .setDetailsBefore(ref.getOriginalClass().getPackageName())
                .setDetailsAfter(ref.getRenamedClass().getPackageName());

        String packageBefore = ref.getOriginalClass().getPackageName();
        String packageAfter = ref.getRenamedClass().getPackageName();

        String fileBefore = ref.getOriginalClass().getSourceFile();
        String fileAfter = ref.getRenamedClass().getSourceFile();

        fileBefore = fileBefore.substring(fileBefore.lastIndexOf("/") + 1);
        final String left = fileBefore.substring(0, fileBefore.lastIndexOf("."));

        fileAfter = fileAfter.substring(fileAfter.lastIndexOf("/") + 1);
        final String right = fileAfter.substring(0, fileAfter.lastIndexOf("."));

        String originalClassName = ref.getOriginalClassName();
        String movedClassName = ref.getRenamedClassName();
        originalClassName = originalClassName.contains(".")
                ? originalClassName.substring(originalClassName.lastIndexOf(".") + 1) : originalClassName;
        movedClassName = movedClassName.contains(".")
                ? movedClassName.substring(movedClassName.lastIndexOf(".") + 1) : movedClassName;

        //check if it is inner class
        if ((!left.equals(originalClassName) && packageBefore.contains(left))
                || (!right.equals(movedClassName) && packageAfter.contains(right))) {
            return info
                    .addMarking(createCodeRangeFromKotlin(ref.getOriginalClass().codeRange()),
                            createCodeRangeFromKotlin(ref.getRenamedClass().codeRange()),
                            null,
                            RefactoringLine.MarkingOption.COLLAPSE,
                            false);
        }
        return info.addMarking(createCodeRangeFromKotlin(ref.getOriginalClass().codeRange()),
                createCodeRangeFromKotlin(ref.getRenamedClass().codeRange()),
                (line) -> line.setWord(new String[]{packageBefore, null, packageAfter}),
                RefactoringLine.MarkingOption.PACKAGE,
                false);
    }
}
