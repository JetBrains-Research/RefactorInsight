package data.types.classes;

import data.Group;
import data.RefactoringInfo;
import data.RefactoringLine;
import data.types.Handler;
import gr.uom.java.xmi.diff.MoveAndRenameClassRefactoring;
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

    String[] nameSpace = ref.getRenamedClass().getName().split("\\.");
    String className = nameSpace[nameSpace.length - 1];
    String[] nameSpace2 = ref.getOriginalClass().getName().split("\\.");
    String className2 = nameSpace2[nameSpace2.length - 1];
    info.addMarking(ref.getOriginalClass().codeRange(), ref.getRenamedClass().codeRange(),
        (line) -> {
          line.setWord(
              new String[] {className2, null, className});
        },
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

    String package1 =
        packageBefore.contains(".")
            ? packageBefore.substring(0, packageBefore.lastIndexOf(".")) : packageBefore;

    String package2 =
        packageAfter.contains(".")
            ? packageAfter.substring(0, packageAfter.lastIndexOf(".")) : packageAfter;

    //check if it is inner class
    if (!left.equals(originalClassName) && !right.equals(movedClassName)
        && packageBefore.contains(left) && packageAfter.contains(right)) {
      if (!package1.equals(package2)) {
        info.addMarking(ref.getOriginalClass().codeRange(), ref.getRenamedClass().codeRange(),
            (line) -> {
              line.setWord(
                  new String[] {package1, null, package2});
            },
            RefactoringLine.MarkingOption.PACKAGE,
            true);
      }
      return info
          .addMarking(ref.getOriginalClass().codeRange(), ref.getRenamedClass().codeRange(),
              (line) -> {
                line.setWord(
                    new String[] {left, null, right});
              },
              RefactoringLine.MarkingOption.CLASS,
              true);
    } else if (!left.equals(originalClassName)
        && packageBefore.contains(left) && !package1.equals(packageAfter)) {
      return info
          .addMarking(ref.getOriginalClass().codeRange(), ref.getRenamedClass().codeRange(),
              (line) -> {
                line.setWord(
                    new String[] {package1, null, packageAfter});
              },
              RefactoringLine.MarkingOption.PACKAGE,
              true);

    } else if (!right.equals(movedClassName)
        && packageAfter.contains(right) && !package2.equals(packageBefore)) {
      return info
          .addMarking(ref.getOriginalClass().codeRange(), ref.getRenamedClass().codeRange(),
              (line) -> {
                line.setWord(
                    new String[] {packageBefore, null, package2});
              },
              RefactoringLine.MarkingOption.PACKAGE,
              true);

    }
    return info
        .addMarking(ref.getOriginalClass().codeRange(), ref.getRenamedClass().codeRange(),
            (line) -> {
              line.setWord(
                  new String[] {packageBefore, null, packageAfter});
            },
            RefactoringLine.MarkingOption.PACKAGE,
            false);

  }
}
