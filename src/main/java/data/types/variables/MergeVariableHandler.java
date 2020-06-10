package data.types.variables;

import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.MergeVariableRefactoring;
import java.util.stream.Collectors;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

public class MergeVariableHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    MergeVariableRefactoring ref = (MergeVariableRefactoring) refactoring;

    ref.getMergedVariables().forEach(var ->
        info.addMarking(var.codeRange(), ref.getNewVariable().codeRange(), true));

    if (ref.getNewVariable().isParameter()) {
      info.setGroup(RefactoringInfo.Group.METHOD)
          .setDetailsBefore(ref.getOperationBefore().getClassName())
          .setDetailsAfter(ref.getOperationAfter().getClassName());
    } else {
      info.setGroup(RefactoringInfo.Group.VARIABLE);
    }

    return info
        .setElementBefore(ref.getMergedVariables().stream().map(x -> x.getVariableName()).collect(
            Collectors.joining()))
        .setElementAfter(ref.getNewVariable().getVariableDeclaration().toQualifiedString())
        .setNameBefore(Utils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(Utils.calculateSignature(ref.getOperationAfter()));
  }
}
