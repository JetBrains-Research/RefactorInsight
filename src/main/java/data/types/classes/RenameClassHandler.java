package data.types.classes;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.RenameClassRefactoring;
import org.refactoringminer.api.Refactoring;

public class RenameClassHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RenameClassRefactoring ref = (RenameClassRefactoring) refactoring;

    return info.setGroup(Group.CLASS)
        .addMarking(ref.getOriginalClass().codeRange().getStartLine(),
            ref.getOriginalClass().codeRange().getStartLine() + 1,
            ref.getRenamedClass().codeRange().getStartLine(),
            ref.getRenamedClass().codeRange().getStartLine() + 1,
            ref.getOriginalClass().codeRange().getFilePath(),
            ref.getRenamedClass().codeRange().getFilePath())
        .setNameBefore(ref.getOriginalClassName())
        .setNameAfter(ref.getRenamedClassName());
  }
}
