package data.types.classes;

import static data.RefactoringLine.MarkingOption.REMOVE;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.RefactoringLine;
import data.types.Handler;
import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.RemoveClassAnnotationRefactoring;
import org.refactoringminer.api.Refactoring;

public class RemoveClassAnnotationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    RemoveClassAnnotationRefactoring ref = (RemoveClassAnnotationRefactoring) refactoring;
    UMLAnnotation annotation = ref.getAnnotation();
    return info.setGroup(Group.CLASS)
        .setNameBefore(ref.getClassBefore().getName())
        .setNameAfter(ref.getClassAfter().getName())
        .setElementBefore(ref.getAnnotation().toString())
        .setElementAfter(null)
        .addMarking(
            annotation.codeRange(),
            ref.getClassBefore().codeRange(),
            line -> line.addOffset(annotation.getLocationInfo(),
                REMOVE),
            REMOVE,
            false);
  }
}
