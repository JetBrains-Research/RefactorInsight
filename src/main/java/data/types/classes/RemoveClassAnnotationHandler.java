package data.types.classes;

import static data.RefactoringLine.MarkingOption.REMOVE;

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
    if (ref.getClassAfter().isAbstract()) {
      info.setGroup(Group.ABSTRACT);
    } else if (ref.getClassAfter().isInterface()) {
      info.setGroup(Group.INTERFACE);
    } else {
      info.setGroup(Group.CLASS);
    }
    return info
        .setNameBefore(ref.getClassBefore().getName())
        .setNameAfter(ref.getClassAfter().getName())
        .setElementBefore(ref.getAnnotation().toString())
        .setElementAfter(null)
        .setDetailsBefore(ref.getClassBefore().getPackageName())
        .setDetailsAfter(ref.getClassAfter().getPackageName())
        .addMarking(
            annotation.codeRange(),
            ref.getClassAfter().codeRange(),
            line -> line.addOffset(annotation.getLocationInfo(),
                REMOVE),
            REMOVE,
            false);
  }
}
