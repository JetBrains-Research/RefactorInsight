package data.types.methods;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.RemoveParameterRefactoring;
import org.refactoringminer.api.Refactoring;

public class RemoveParameterHandler extends Handler {
  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RemoveParameterRefactoring ref = (RemoveParameterRefactoring) refactoring;
    return info.setGroup(Group.METHOD)
        .setNameBefore(calculateSignature(ref.getOperationBefore()))
        .setNameAfter(calculateSignature(ref.getOperationAfter()))
        .setElementBefore(ref.getParameter().getVariableDeclaration().toQualifiedString())
        .setElementAfter(null)
        .addMarking(ref.getOperationBefore().codeRange(), ref.getOperationAfter().codeRange(),
            line -> line.addOffset(
                ref.getParameter().getVariableDeclaration().getLocationInfo().getStartOffset(),
                ref.getParameter().getVariableDeclaration().getLocationInfo().getEndOffset(),
                0, 0));
  }
}
