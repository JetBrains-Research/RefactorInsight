package data.types.variables;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.RenameVariableRefactoring;
import org.refactoringminer.api.Refactoring;

public class RenameVariableHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RenameVariableRefactoring ref = (RenameVariableRefactoring) refactoring;
    return info.setGroup(Group.VARIABLE)
        .addMarking(ref.getOriginalVariable().codeRange(),
            ref.getRenamedVariable().codeRange());
  }
}
