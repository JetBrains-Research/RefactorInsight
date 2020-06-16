package data.types.classes;

import data.Group;
import data.RefactoringInfo;
import data.RefactoringLine;
import data.types.Handler;
import gr.uom.java.xmi.diff.MoveClassRefactoring;
import org.refactoringminer.api.Refactoring;

public class MoveClassHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
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
    final String left = fileBefore.substring(0, fileBefore.lastIndexOf("."));

    fileAfter = fileAfter.substring(fileAfter.lastIndexOf("/") + 1);
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
        info.addMarking(ref.getOriginalClass().codeRange(), ref.getMovedClass().codeRange(),
            (line) -> {
              line.setWord(
                  new String[] {package1, null, package2});
            },
            RefactoringLine.MarkingOption.PACKAGE,
            true);
      }
      return info
          .addMarking(ref.getOriginalClass().codeRange(), ref.getMovedClass().codeRange(),
              (line) -> {
                line.setWord(
                    new String[] {left, null, right});
              },
              RefactoringLine.MarkingOption.CLASS,
              true);
    } else if (!left.equals(originalClassName) && packageBefore.contains(left)) {
      if (!package1.equals(packageAfter)) {
        info
            .addMarking(ref.getOriginalClass().codeRange(), ref.getMovedClass().codeRange(),
                (line) -> {
                  line.setWord(
                      new String[] {package1, null, packageAfter});
                },
                RefactoringLine.MarkingOption.PACKAGE,
                true);
      }
      return info.addMarking(ref.getOriginalClass().codeRange(), ref.getMovedClass().codeRange(),
          null,
          RefactoringLine.MarkingOption.COLLAPSE,
          false);
    } else if (!right.equals(movedClassName) && packageAfter.contains(right)) {
      if (!package2.equals(packageBefore)) {
        info
            .addMarking(ref.getOriginalClass().codeRange(), ref.getMovedClass().codeRange(),
                (line) -> {
                  line.setWord(
                      new String[] {packageBefore, null, package2});
                },
                RefactoringLine.MarkingOption.PACKAGE,
                true);
      }
      return info.addMarking(ref.getOriginalClass().codeRange(), ref.getMovedClass().codeRange(),
          null,
          RefactoringLine.MarkingOption.COLLAPSE,
          false);
    }
    return info
        .addMarking(ref.getOriginalClass().codeRange(), ref.getMovedClass().codeRange(),
            (line) -> {
              line.setWord(
                  new String[] {packageBefore, null, packageAfter});
            },
            RefactoringLine.MarkingOption.PACKAGE,
            true);
  }
}
