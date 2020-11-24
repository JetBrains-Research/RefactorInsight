package org.jetbrains.research.refactorinsight.data.types.classes;

import gr.uom.java.xmi.diff.MoveAndRenameClassRefactoring;
import org.jetbrains.research.refactorinsight.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.refactoringminer.api.Refactoring;

public class MoveRenameClassHandler extends Handler {

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
    info.addMarking(new CodeRange(ref.getOriginalClass().codeRange()), new CodeRange(ref.getRenamedClass().codeRange()),
        (line) -> line.setWord(
            new String[]{classNameBefore, null, classNameAfter}),
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
          .addMarking(new CodeRange(ref.getOriginalClass().codeRange()),
              new CodeRange(ref.getRenamedClass().codeRange()),
              null,
              RefactoringLine.MarkingOption.COLLAPSE,
              false);
    }
    return info
        .addMarking(new CodeRange(ref.getOriginalClass().codeRange()), new CodeRange(ref.getRenamedClass().codeRange()),
            (line) -> line.setWord(
                new String[]{packageBefore, null, packageAfter}),
            RefactoringLine.MarkingOption.PACKAGE,
            false);

  }

  @Override
  public RefactoringInfo specify(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring,
                                 RefactoringInfo info) {
    org.jetbrains.research.kotlinrminer.diff.refactoring.MoveAndRenameClassRefactoring ref =
        (org.jetbrains.research.kotlinrminer.diff.refactoring.MoveAndRenameClassRefactoring) refactoring;
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
    info.addMarking(new CodeRange(ref.getOriginalClass().codeRange()), new CodeRange(ref.getRenamedClass().codeRange()),
        (line) -> line.setWord(
            new String[]{classNameBefore, null, classNameAfter}),
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
          .addMarking(new CodeRange(ref.getOriginalClass().codeRange()),
              new CodeRange(ref.getRenamedClass().codeRange()),
              null,
              RefactoringLine.MarkingOption.COLLAPSE,
              false);
    }
    return info
        .addMarking(new CodeRange(ref.getOriginalClass().codeRange()), new CodeRange(ref.getRenamedClass().codeRange()),
            (line) -> line.setWord(
                new String[]{packageBefore, null, packageAfter}),
            RefactoringLine.MarkingOption.PACKAGE,
            false);
  }
}
