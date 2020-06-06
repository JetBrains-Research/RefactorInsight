package data.types.methods;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.RefactoringLine;
import data.types.Handler;
import gr.uom.java.xmi.diff.RemoveParameterRefactoring;
import org.refactoringminer.api.Refactoring;

public class RemoveParameterHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    RemoveParameterRefactoring ref = (RemoveParameterRefactoring) refactoring;
    return info.setGroup(Group.METHOD)
        .setNameBefore(calculateSignature(ref.getOperationBefore()))
        .setNameAfter(calculateSignature(ref.getOperationAfter()))
        .setElementBefore(ref.getParameter().getVariableDeclaration().toQualifiedString())
        .setElementAfter(null)
        .addMarking(ref.getOperationBefore().codeRange(), ref.getOperationAfter().codeRange(),
            line -> line.addOffset(
                ref.getParameter().getVariableDeclaration().getLocationInfo(),
                RefactoringLine.MarkingOption.REMOVE)
                .setHasColumns(false));
  }
}
