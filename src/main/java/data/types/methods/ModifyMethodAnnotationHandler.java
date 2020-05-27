package data.types.methods;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.ModifyMethodAnnotationRefactoring;
import org.refactoringminer.api.Refactoring;

public class ModifyMethodAnnotationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ModifyMethodAnnotationRefactoring ref = (ModifyMethodAnnotationRefactoring) refactoring;
    return info.setGroup(Group.METHOD)
        .setElementBefore(ref.getAnnotationBefore().toString())
        .setElementAfter(ref.getAnnotationAfter().toString())
        .addMarking(ref.getAnnotationBefore().codeRange(), ref.getAnnotationAfter().codeRange(),
                line -> line.addOffset(ref.getAnnotationBefore().getLocationInfo(),
                        ref.getAnnotationAfter().getLocationInfo()))
        .setNameBefore(calculateSignature(ref.getOperationBefore()))
        .setNameAfter(calculateSignature(ref.getOperationAfter()));
  }
}
