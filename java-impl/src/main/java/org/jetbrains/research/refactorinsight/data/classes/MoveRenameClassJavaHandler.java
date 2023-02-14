package org.jetbrains.research.refactorinsight.data.classes;

import gr.uom.java.xmi.diff.MoveAndRenameClassRefactoring;
import org.jetbrains.research.refactorinsight.data.*;
import org.jetbrains.research.refactorinsight.data.util.JavaUtils;
import org.refactoringminer.api.Refactoring;

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

        info.addMarking(JavaUtils.createCodeRangeFromJava(ref.getOriginalClass().codeRange()),
                        JavaUtils.createCodeRangeFromJava(ref.getRenamedClass().codeRange()),
                        (line) -> line.setWord(new String[]{classNameBefore, null, classNameAfter}),
                        RefactoringLine.MarkingOption.COLLAPSE,
                        false)
                .setNameBefore(ref.getOriginalClassName())
                .setNameAfter(ref.getRenamedClassName())
                .setDetailsBefore(ref.getOriginalClass().getName())
                .setDetailsAfter(ref.getRenamedClass().getName());

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

        info.setFoldingDescriptorBefore(FoldingBuilder.fromClass(ref.getOriginalClass()));
        info.setFoldingDescriptorAfter(FoldingBuilder.fromClass(ref.getMovedClass()));

        //check if it is inner class
        if ((!left.equals(originalClassName) && packageBefore.contains(left))
                || (!right.equals(movedClassName) && packageAfter.contains(right))) {
            return info.addMarking(JavaUtils.createCodeRangeFromJava(ref.getOriginalClass().codeRange()),
                    JavaUtils.createCodeRangeFromJava(ref.getRenamedClass().codeRange()),
                    null,
                    RefactoringLine.MarkingOption.COLLAPSE,
                    false);
        }

        return info.addMarking(JavaUtils.createCodeRangeFromJava(ref.getOriginalClass().codeRange()),
                JavaUtils.createCodeRangeFromJava(ref.getRenamedClass().codeRange()),
                (line) -> line.setWord(new String[]{packageBefore, null, packageAfter}),
                RefactoringLine.MarkingOption.PACKAGE,
                false);
    }

}
