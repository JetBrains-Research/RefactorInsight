package data.types.methods;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.AddParameterRefactoring;
import org.refactoringminer.api.Refactoring;

public class AddParameterHandler extends Handler {
  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    AddParameterRefactoring ref = (AddParameterRefactoring) refactoring;
    return info.setGroup(Group.METHOD)
        .setNameBefore(calculateSignature(ref.getOperationBefore()))
        .setNameAfter(calculateSignature(ref.getOperationAfter()))
        .setElementBefore(ref.getParameter().getVariableDeclaration().toQualifiedString())
        .setElementAfter(null)
        .addMarking(ref.getOperationBefore().codeRange(), ref.getOperationAfter().codeRange(),
            line -> line.addOffset(0, 0,
                ref.getParameter().getVariableDeclaration().getLocationInfo().getStartOffset(),
                ref.getParameter().getVariableDeclaration().getLocationInfo().getEndOffset()));
  }
}
