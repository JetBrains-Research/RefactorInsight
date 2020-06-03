package data.types.methods;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ReorderParameterRefactoring;
import org.refactoringminer.api.Refactoring;

public class ReorderParameterHandler extends Handler {
  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ReorderParameterRefactoring ref = (ReorderParameterRefactoring) refactoring;
    return info.setGroup(Group.METHOD)
        .setNameBefore(calculateSignature(ref.getOperationBefore()))
        .setNameAfter(calculateSignature(ref.getOperationAfter()))
        .setElementBefore(ref.getOperationBefore().toQualifiedString())
        .setElementAfter(ref.getOperationAfter().toQualifiedString())
        .addMarking(ref.getOperationBefore().codeRange(), ref.getOperationAfter().codeRange(),
            line -> line.addOffset(
                ref.getOperationBefore().getLocationInfo(),
                ref.getOperationAfter().getLocationInfo()));
  }
}
