package data.types.attributes;

import static data.RefactoringLine.MarkingOption.ADD;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.AddAttributeAnnotationRefactoring;
import org.refactoringminer.api.Refactoring;

public class AddAttributeAnnotationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    AddAttributeAnnotationRefactoring ref = (AddAttributeAnnotationRefactoring) refactoring;
    UMLAnnotation annotation = ref.getAnnotation();

    return info.setGroup(Group.ATTRIBUTE)
        .setNameBefore(ref.getAttributeBefore().toQualifiedString())
        .setNameAfter(ref.getAttributeAfter().toQualifiedString())
        .setElementBefore(ref.getAnnotation().toString())
        .setElementAfter(null)
        .addMarking(
            ref.getAttributeBefore().codeRange(),
            annotation.codeRange(),
            line -> line.addOffset(annotation.getLocationInfo(), ADD),
            ADD,
            false);
  }
}
