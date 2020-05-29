package data.types.methods;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.AddParameterRefactoring;
import org.refactoringminer.api.Refactoring;

public class AddParameterHandler extends Handler {
  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    AddParameterRefactoring ref = (AddParameterRefactoring) refactoring;
    return info.setGroup(Group.METHOD)
        .setNameBefore(calculateSignature(ref.getOperationBefore()))
        .setNameAfter(calculateSignature(ref.getOperationAfter()))
        .setElementBefore(ref.getParameter().getVariableDeclaration().toQualifiedString())
        .setElementAfter(null);
  }
}
