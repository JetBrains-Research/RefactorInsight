package org.jetbrains.research.refactorinsight.java.impl.data.classes;

import gr.uom.java.xmi.diff.MoveAndRenameClassRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.java.api.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.java.api.util.Utils.createCodeRangeFromJava;

public class MoveRenameClassJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
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

        info.addMarking(createCodeRangeFromJava(ref.getOriginalClass().codeRange()),
                        createCodeRangeFromJava(ref.getRenamedClass().codeRange()),
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
            return info.addMarking(createCodeRangeFromJava(ref.getOriginalClass().codeRange()),
                    createCodeRangeFromJava(ref.getRenamedClass().codeRange()),
                    null,
                    RefactoringLine.MarkingOption.COLLAPSE,
                    false);
        }

        return info.addMarking(createCodeRangeFromJava(ref.getOriginalClass().codeRange()),
                createCodeRangeFromJava(ref.getRenamedClass().codeRange()),
                (line) -> line.setWord(new String[]{packageBefore, null, packageAfter}),
                RefactoringLine.MarkingOption.PACKAGE,
                false);
    }

}
