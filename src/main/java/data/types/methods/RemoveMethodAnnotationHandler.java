package data.types.methods;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.RemoveMethodAnnotationRefactoring;
import org.refactoringminer.api.Refactoring;

public class RemoveMethodAnnotationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RemoveMethodAnnotationRefactoring ref = (RemoveMethodAnnotationRefactoring) refactoring;
    UMLAnnotation annotation = ref.getAnnotation();
    return info.setGroup(Group.METHOD)
        .setElementBefore(ref.getAnnotation().toString())
        .setElementAfter(null)
        .addMarking(annotation.getLocationInfo().getStartLine(),
                annotation.getLocationInfo().getEndLine(),
                ref.getOperationBefore().codeRange().getStartLine(),
                ref.getOperationBefore().codeRange().getStartLine() - 1,
                ref.getOperationBefore().codeRange().getFilePath(),
                annotation.getLocationInfo().getFilePath(),
                line -> line.addOffset(annotation.getLocationInfo().getStartOffset(),
                        annotation.getLocationInfo().getEndOffset(),
                        0, 0))
        .setNameBefore(calculateSignature(ref.getOperationBefore()))
        .setNameAfter(calculateSignature(ref.getOperationAfter()));
  }
}
