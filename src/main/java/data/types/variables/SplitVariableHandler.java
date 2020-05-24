package data.types.variables;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.SplitVariableRefactoring;
import java.util.stream.Collectors;
import org.refactoringminer.api.Refactoring;

public class SplitVariableHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    SplitVariableRefactoring ref = (SplitVariableRefactoring) refactoring;

    ref.getSplitVariables().forEach(var ->
        info.addMarking(ref.getOldVariable().codeRange(), var.codeRange()));

    return info.setGroup(Group.VARIABLE).setNameBefore(ref.getOldVariable().getVariableName())
        .setNameAfter(ref.getSplitVariables().stream().map(x -> x.getVariableName()).collect(
            Collectors.joining()));
  }
}
