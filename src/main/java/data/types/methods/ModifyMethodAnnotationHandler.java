package data.types.methods;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ModifyMethodAnnotationRefactoring;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

public class ModifyMethodAnnotationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    ModifyMethodAnnotationRefactoring ref = (ModifyMethodAnnotationRefactoring) refactoring;
    return info.setGroup(Group.METHOD)
        .setElementBefore(ref.getAnnotationBefore().toString())
        .setElementAfter(ref.getAnnotationAfter().toString())
        .addMarking(ref.getAnnotationBefore().codeRange(), ref.getAnnotationAfter().codeRange())
        .setNameBefore(Utils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(Utils.calculateSignature(ref.getOperationAfter()));
  }
}
