package data.types.methods;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.AddMethodAnnotationRefactoring;
import org.refactoringminer.api.Refactoring;

public class AddMethodAnnotationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    AddMethodAnnotationRefactoring ref = (AddMethodAnnotationRefactoring) refactoring;
    return info.setGroup(Group.METHOD)
        .setElementBefore(ref.getAnnotation().toString())
        .setElementAfter(null)
        .addMarking(ref.getOperationBefore().codeRange(), ref.getOperationAfter().codeRange())
        .addMarking(ref.getOperationBefore().codeRange().getStartLine(),
                ref.getOperationBefore().codeRange().getStartLine() - 1,
                ref.getAnnotation().getLocationInfo().getStartLine(),
                ref.getAnnotation().getLocationInfo().getEndLine(),
                ref.getOperationBefore().codeRange().getFilePath(),
                ref.getAnnotation().getLocationInfo().getFilePath())
        .setNameBefore(calculateSignature(ref.getOperationBefore()))
        .setNameAfter(calculateSignature(ref.getOperationBefore()));
  }
}
