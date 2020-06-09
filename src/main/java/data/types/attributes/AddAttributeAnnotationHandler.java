package data.types.attributes;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.AddAttributeAnnotationRefactoring;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

public class AddAttributeAnnotationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    AddAttributeAnnotationRefactoring ref = (AddAttributeAnnotationRefactoring) refactoring;
    UMLAnnotation annotation = ref.getAnnotation();

    String classNameBefore = ref.getAttributeBefore().getClassName();
    String classNameAfter = ref.getAttributeAfter().getClassName();

    return info.setGroup(Group.ATTRIBUTE)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setNameBefore(ref.getAttributeBefore().getVariableDeclaration().toQualifiedString())
        .setNameAfter(ref.getAttributeAfter().getVariableDeclaration().toQualifiedString())
        .setElementBefore(ref.getAnnotation().toString())
        .setElementAfter(null)
        .addMarking(ref.getAttributeBefore().codeRange().getStartLine(),
            ref.getAttributeAfter().codeRange().getStartLine() - 1,
            annotation.getLocationInfo().getStartLine(),
            annotation.getLocationInfo().getEndLine(),
            ref.getAttributeBefore().codeRange().getFilePath(),
            annotation.getLocationInfo().getFilePath(),
            line -> line.addOffset(1, 1,
                annotation.getLocationInfo().getStartOffset(),
                annotation.getLocationInfo().getEndOffset()));
  }
}
