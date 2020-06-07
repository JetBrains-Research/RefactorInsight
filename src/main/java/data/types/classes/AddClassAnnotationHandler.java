package data.types.classes;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.RefactoringLine;
import data.types.Handler;
import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.AddClassAnnotationRefactoring;
import org.refactoringminer.api.Refactoring;


public class AddClassAnnotationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    AddClassAnnotationRefactoring ref = (AddClassAnnotationRefactoring) refactoring;
    UMLAnnotation annotation = ref.getAnnotation();
    return info.setGroup(Group.CLASS)
        .setNameBefore(ref.getClassBefore().getName())
        .setNameAfter(ref.getClassAfter().getName())
        .setElementBefore(ref.getAnnotation().toString())
        .setElementAfter(null)
        .addMarking(
            ref.getClassBefore().codeRange(),
            annotation.codeRange(),
            line -> line.addOffset(//TODO was (0,0) not 11
                annotation.getLocationInfo(), RefactoringLine.MarkingOption.ADD),
            RefactoringLine.MarkingOption.ADD,
            false);
  }
}
