package data.types.methods;

import data.Group;
import data.RefactoringInfo;
import data.RefactoringLine;
import data.types.Handler;
import gr.uom.java.xmi.diff.RemoveParameterRefactoring;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

public class RemoveParameterHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RemoveParameterRefactoring ref = (RemoveParameterRefactoring) refactoring;
    return info.setGroup(Group.METHOD)
        .setNameBefore(Utils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(Utils.calculateSignature(ref.getOperationAfter()))
        .setElementBefore(ref.getParameter().getVariableDeclaration().toQualifiedString())
        .setElementAfter(null)
        .addMarking(ref.getOperationBefore().codeRange(), ref.getOperationAfter().codeRange(),
            line -> line.addOffset(
                ref.getParameter().getVariableDeclaration().getLocationInfo(),
                RefactoringLine.MarkingOption.REMOVE)
                .setHasColumns(false),
            RefactoringLine.MarkingOption.NONE,true);
  }
}
