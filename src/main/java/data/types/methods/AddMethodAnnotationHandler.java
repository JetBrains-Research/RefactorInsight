package data.types.methods;

import static data.RefactoringLine.MarkingOption.ADD;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.RefactoringLine;
import data.types.Handler;
import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.AddMethodAnnotationRefactoring;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

public class AddMethodAnnotationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    AddMethodAnnotationRefactoring ref = (AddMethodAnnotationRefactoring) refactoring;
    UMLAnnotation annotation = ref.getAnnotation();
    return info.setGroup(Group.METHOD)
        .setElementBefore(annotation.toString())
        .setElementAfter(null)
        .addMarking(
            ref.getOperationBefore().codeRange(),
            annotation.codeRange(),
            line -> line.addOffset(//TODO 00 again instead of 11
                annotation.getLocationInfo(),
                ADD),
            ADD,
            false)
        .setNameBefore(Utils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(Utils.calculateSignature(ref.getOperationBefore()));
  }
}
