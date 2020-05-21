package data.types.methods;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ModifyMethodAnnotationRefactoring;
import org.refactoringminer.api.Refactoring;

public class ModifyMethodAnnotationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ModifyMethodAnnotationRefactoring ref = (ModifyMethodAnnotationRefactoring) refactoring;
    return info.setGroup(Group.METHOD)
        .addMarking(ref.getAnnotationBefore().codeRange(), ref.getAnnotationAfter().codeRange())
        .setNameBefore(calculateSignature(ref.getOperationBefore()))
        .setNameAfter(calculateSignature(ref.getOperationAfter()));
  }
}
