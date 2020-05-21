package data.types.methods;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.RemoveMethodAnnotationRefactoring;
import org.refactoringminer.api.Refactoring;

public class RemoveMethodAnnotationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RemoveMethodAnnotationRefactoring ref = (RemoveMethodAnnotationRefactoring) refactoring;
    return info.setGroup(Group.METHOD)
        .addMarking(ref.getAnnotation().codeRange(),
            ref.getOperationAfter().codeRange())
        .setNameBefore(calculateSignature(ref.getOperationBefore()))
        .setNameAfter(calculateSignature(ref.getOperationAfter()));
  }
}
