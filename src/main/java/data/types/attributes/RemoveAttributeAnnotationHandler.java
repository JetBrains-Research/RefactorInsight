package data.types.attributes;

import static data.RefactoringLine.MarkingOption.REMOVE;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.RemoveAttributeAnnotationRefactoring;
import org.refactoringminer.api.Refactoring;

public class RemoveAttributeAnnotationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RemoveAttributeAnnotationRefactoring ref = (RemoveAttributeAnnotationRefactoring) refactoring;
    UMLAnnotation annotation = ref.getAnnotation();
    return info.setGroup(Group.ATTRIBUTE)
        .setNameBefore(ref.getAttributeBefore().toQualifiedString())
        .setNameAfter(ref.getAttributeAfter().toQualifiedString())
        .setElementBefore(ref.getAnnotation().toString())
        .setElementAfter(null)
        .addMarking(
            annotation.codeRange(),
            ref.getAttributeAfter().codeRange(),
            line -> line.addOffset(annotation.getLocationInfo(), REMOVE),
            REMOVE,
            false);
  }
}
