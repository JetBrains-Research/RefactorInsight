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

    return info
        .addMarking(ref.getOriginalClass().codeRange(), ref.getRenamedClass().codeRange(),
            (line) -> {
              line.setWord(
                  new String[] {ref.getOriginalClass().getPackageName(), null,
                      ref.getRenamedClass().getPackageName()});
            },
            RefactoringLine.MarkingOption.PACKAGE,
            true)
        .addMarking(ref.getOriginalClass().codeRange(), ref.getRenamedClass().codeRange(),
            (line) -> {
              line.setWord(
                  new String[] {className2, null, className});
            },
            RefactoringLine.MarkingOption.COLLAPSE,
            true)
        .setNameBefore(ref.getOriginalClassName())
        .setNameAfter(ref.getRenamedClassName())
        .setDetailsBefore(ref.getOriginalClass().getPackageName())
        .setDetailsAfter(ref.getRenamedClass().getPackageName());
  }
}
