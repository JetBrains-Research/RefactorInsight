package data.types.methods;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.AddMethodAnnotationRefactoring;
import org.refactoringminer.api.Refactoring;
import utils.StringUtils;

public class AddMethodAnnotationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    AddMethodAnnotationRefactoring ref = (AddMethodAnnotationRefactoring) refactoring;
    UMLAnnotation annotation = ref.getAnnotation();

    String classNameBefore = ref.getOperationBefore().getClassName();
    String classNameAfter = ref.getOperationAfter().getClassName();

    return info.setGroup(Group.METHOD)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setElementBefore(annotation.toString())
        .setElementAfter(null)
        .addMarking(ref.getOperationBefore().codeRange().getStartLine(),
            ref.getOperationBefore().codeRange().getStartLine() - 1,
            annotation.getLocationInfo().getStartLine(),
            annotation.getLocationInfo().getEndLine(),
            ref.getOperationBefore().codeRange().getFilePath(),
            annotation.getLocationInfo().getFilePath(),
            line -> line.addOffset(0, 0,
                annotation.getLocationInfo().getStartOffset(),
                annotation.getLocationInfo().getEndOffset()))
        .setNameBefore(StringUtils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(StringUtils.calculateSignature(ref.getOperationAfter()));
  }
}
