package data.types.variables;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.MergeVariableRefactoring;
import org.refactoringminer.api.Refactoring;

public class MergeVariableHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    MergeVariableRefactoring ref = (MergeVariableRefactoring) refactoring;

    ref.getMergedVariables().forEach(var ->
        info.addMarking(var.codeRange(), ref.getNewVariable().codeRange()));

    return info.setGroup(Group.VARIABLE);
  }
}
