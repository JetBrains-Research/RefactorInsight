package data.types.classes;

import data.RefactoringInfo;
import data.RefactoringInfo.Group;
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

    return info
        .addMarking(ref.getOriginalClass().codeRange(), ref.getMovedClass().codeRange(),
            (line) -> {
              line.setLazilyHighlightableWords(
                  new String[] {packageBefore, null, packageAfter});
            },
            RefactoringLine.MarkingOption.PACKAGE,
            true)
        .setNameBefore(ref.getOriginalClassName())
        .setNameAfter(ref.getMovedClassName())
        .setDetailsBefore(ref.getOriginalClass().getPackageName())
        .setDetailsAfter(ref.getMovedClass().getPackageName());
  }
}
