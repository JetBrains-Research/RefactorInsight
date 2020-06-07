package data.types.variables;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.InlineVariableRefactoring;
import org.refactoringminer.api.Refactoring;

public class InlineVariableHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    InlineVariableRefactoring ref = (InlineVariableRefactoring) refactoring;
    return info.setGroup(Group.VARIABLE)
        .setElementBefore(" in method " + ref.getOperationAfter().getName())
        .setElementAfter(null)
        .setNameBefore(ref.getVariableDeclaration().toQualifiedString())
        .setNameAfter(ref.getVariableDeclaration().toQualifiedString())
        .addMarking(ref.getVariableDeclaration().codeRange(),
            ref.getInlinedVariableDeclarationCodeRange(), true);


  }
}
