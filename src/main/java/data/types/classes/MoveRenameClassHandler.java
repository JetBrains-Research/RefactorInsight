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

    //check if it is inner class
    if ((!left.equals(originalClassName) && packageBefore.contains(left))
        || (!right.equals(movedClassName) && packageAfter.contains(right))){
      return info
          .addMarking(ref.getOriginalClass().codeRange(), ref.getRenamedClass().codeRange(),
              null,
              RefactoringLine.MarkingOption.COLLAPSE,
              false);
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
