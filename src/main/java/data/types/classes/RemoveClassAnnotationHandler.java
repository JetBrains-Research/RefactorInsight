package data.types.classes;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.RemoveClassAnnotationRefactoring;
import org.refactoringminer.api.Refactoring;

public class RemoveClassAnnotationHandler extends Handler {
  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RemoveClassAnnotationRefactoring ref = (RemoveClassAnnotationRefactoring) refactoring;
    UMLAnnotation annotation = ref.getAnnotation();
    return info.setGroup(Group.CLASS)
        .setNameBefore(ref.getClassBefore().getName())
        .setNameAfter(ref.getClassAfter().getName())
        .setElementBefore(ref.getAnnotation().toString())
        .setElementAfter(null)
        .addMarking(annotation.getLocationInfo().getStartLine(),
            annotation.getLocationInfo().getEndLine(),
            ref.getClassBefore().codeRange().getStartLine(),
            ref.getClassBefore().codeRange().getStartLine() - 1,
            ref.getClassBefore().codeRange().getFilePath(),
            annotation.getLocationInfo().getFilePath(),
            line -> line.addOffset(annotation.getLocationInfo().getStartOffset(),
                annotation.getLocationInfo().getEndOffset(),
                0, 0));
  }
}
