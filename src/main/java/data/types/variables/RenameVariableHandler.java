package data.types.variables;

import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.RenameVariableRefactoring;
import org.refactoringminer.api.Refactoring;
import utils.StringUtils;

public class RenameVariableHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RenameVariableRefactoring ref = (RenameVariableRefactoring) refactoring;
    String id = ref.getOperationAfter().getClassName() + ".";
    if ((ref.getOperationAfter().isConstructor() || ref.getOperationAfter().isSetter())
        && ref.getRenamedVariable().isParameter()) {
      id += ref.getRenamedVariable().getVariableName();
    } else {
      id = StringUtils.calculateSignature(ref.getOperationAfter()) + "."
          + ref.getRenamedVariable().getVariableName();
    }
    info.setGroupId(id);

    if (ref.getRenamedVariable().isParameter()) {
      info.setGroup(RefactoringInfo.Group.METHOD)
          .setDetailsBefore(ref.getOperationBefore().getClassName())
          .setDetailsAfter(ref.getOperationAfter().getClassName());
    } else {
      info.setGroup(RefactoringInfo.Group.VARIABLE);
    }

    return info
        .setElementBefore(ref.getOriginalVariable().getVariableDeclaration().toQualifiedString())
        .setElementAfter(ref.getRenamedVariable().getVariableDeclaration().toQualifiedString())
        .setNameBefore(StringUtils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(StringUtils.calculateSignature(ref.getOperationAfter()))
        .addMarking(ref.getOriginalVariable().getVariableDeclaration().codeRange(),
            ref.getRenamedVariable().codeRange(), true);
  }
}
