package data.types.methods;

import data.Group;
import data.RefactoringInfo;
import data.RefactoringLine.MarkingOption;
import data.types.Handler;
import gr.uom.java.xmi.diff.AddParameterRefactoring;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

public class AddParameterHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    AddParameterRefactoring ref = (AddParameterRefactoring) refactoring;
    return info.setGroup(Group.METHOD)
        .setNameBefore(Utils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(Utils.calculateSignature(ref.getOperationAfter()))
        .setElementAfter(null)
        .setElementBefore(ref.getParameter().getVariableDeclaration().toQualifiedString())
        .addMarking(ref.getOperationBefore().codeRange(), ref.getOperationAfter().codeRange(),
            line -> line.addOffset(
                ref.getParameter().getVariableDeclaration().getLocationInfo(), MarkingOption.ADD),
            MarkingOption.NONE,
            true);
  }
}
