package data.types.methods;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.AddParameterRefactoring;
import gr.uom.java.xmi.diff.ReorderParameterRefactoring;
import org.refactoringminer.api.Refactoring;

public class ReorderParameterHandler extends Handler {
  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    ReorderParameterRefactoring ref = (ReorderParameterRefactoring) refactoring;
    return info.setGroup(Group.METHOD)
            .setNameBefore(calculateSignature(ref.getOperationBefore()))
            .setNameAfter(calculateSignature(ref.getOperationAfter()))
            .setElementBefore(ref.getOperationBefore().toQualifiedString())
            .setElementAfter(ref.getOperationAfter().toQualifiedString())
            .addMarking(ref.getOperationBefore().codeRange(), ref.getOperationAfter().codeRange());
  }
}
