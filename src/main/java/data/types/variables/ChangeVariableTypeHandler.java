package data.types.variables;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ChangeVariableTypeRefactoring;
import org.refactoringminer.api.Refactoring;

public class ChangeVariableTypeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    ChangeVariableTypeRefactoring ref = (ChangeVariableTypeRefactoring) refactoring;
    //TODO ref.getRalatedRefactorings might help in combining refactorings such as
    //TODO renaming variables and corresponding methods
    return info.setGroup(Group.VARIABLE)
        .setNameBefore(
            ref.getOriginalVariable().getVariableName() + " in method "
                + ref.getOperationAfter().getName())
        .setNameAfter(
            ref.getOriginalVariable().getVariableName() + " in method "
                + ref.getOperationAfter().getName())
        .setElementBefore(ref.getOriginalVariable().toQualifiedString())
        .setElementAfter(ref.getChangedTypeVariable().toQualifiedString())
        .addMarking(ref.getOriginalVariable().getType().codeRange(),
            ref.getChangedTypeVariable().getType().codeRange());
  }
}
