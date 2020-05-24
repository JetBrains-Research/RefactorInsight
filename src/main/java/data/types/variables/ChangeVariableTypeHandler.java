package data.types.variables;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ChangeVariableTypeRefactoring;
import org.refactoringminer.api.Refactoring;

public class ChangeVariableTypeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
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
        .addMarking(ref.getOriginalVariable().codeRange(),
            ref.getChangedTypeVariable().codeRange());
  }
}
