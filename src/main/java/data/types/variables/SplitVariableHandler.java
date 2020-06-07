package data.types.variables;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.SplitVariableRefactoring;
import java.util.stream.Collectors;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

public class SplitVariableHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    SplitVariableRefactoring ref = (SplitVariableRefactoring) refactoring;

    ref.getSplitVariables().forEach(var ->
        info.addMarking(ref.getOldVariable().codeRange(), var.codeRange(), true));

    if (ref.getOldVariable().isParameter()) {
      info.setGroup(Group.METHOD)
          .setDetailsBefore(ref.getOperationBefore().getClassName())
          .setDetailsAfter(ref.getOperationAfter().getClassName());
    } else {
      info.setGroup(Group.VARIABLE);
    }

    return info
        .setNameBefore(Utils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(Utils.calculateSignature(ref.getOperationAfter()))
        .setElementBefore(ref.getOldVariable().getVariableDeclaration().toQualifiedString())
        .setElementAfter(ref.getSplitVariables().stream().map(x -> x.getVariableName()).collect(
            Collectors.joining()));
  }
}
