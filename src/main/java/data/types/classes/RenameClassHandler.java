package data.types.classes;

import data.RefactoringInfo;
import data.RefactoringLine;
import data.types.Handler;
import gr.uom.java.xmi.diff.RenameClassRefactoring;
import org.refactoringminer.api.Refactoring;

public class RenameClassHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RenameClassRefactoring ref = (RenameClassRefactoring) refactoring;

    if (ref.getRenamedClass().isAbstract()) {
      info.setGroup(RefactoringInfo.Group.ABSTRACT);
    } else if (ref.getRenamedClass().isInterface()) {
      info.setGroup(RefactoringInfo.Group.INTERFACE);
    } else {
      info.setGroup(RefactoringInfo.Group.CLASS);
    }
    String[] nameSpace = ref.getRenamedClass().getName().split("\\.");
    String className = nameSpace[nameSpace.length - 1];
    return info
        .addMarking(ref.getOriginalClass().codeRange(), ref.getRenamedClass().codeRange(),
            (line) -> {
              line.setLazilyHighlightableWords(
                  new String[] {null, null, className});
            },
            RefactoringLine.MarkingOption.COLLAPSE,
            true)
        .setNameBefore(ref.getOriginalClassName())
        .setNameAfter(ref.getRenamedClassName())
        .setDetailsBefore(ref.getOriginalClass().getPackageName())
        .setDetailsAfter(ref.getRenamedClass().getPackageName())
        .setElementBefore(null)
        .setElementAfter(null);
  }
}
