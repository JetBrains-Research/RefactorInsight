package data.types.variables;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.decomposition.VariableDeclaration;
import gr.uom.java.xmi.diff.MergeVariableRefactoring;
import java.util.stream.Collectors;
import org.refactoringminer.api.Refactoring;
import utils.StringUtils;

public class MergeVariableHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    MergeVariableRefactoring ref = (MergeVariableRefactoring) refactoring;

    ref.getMergedVariables().forEach(var ->
        info.addMarking(var.codeRange(), ref.getNewVariable().codeRange(), true));

    if (ref.getNewVariable().isParameter()) {
      info.setGroup(Group.METHOD)
          .setDetailsBefore(ref.getOperationBefore().getClassName())
          .setDetailsAfter(ref.getOperationAfter().getClassName());
    } else {
      info.setGroup(Group.VARIABLE);
    }

    return info
        .setElementBefore(ref.getMergedVariables().stream().map(
            VariableDeclaration::getVariableName).collect(
            Collectors.joining()))
        .setElementAfter(ref.getNewVariable().getVariableDeclaration().toQualifiedString())
        .setNameBefore(StringUtils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(StringUtils.calculateSignature(ref.getOperationAfter()));
  }
}
