package data.types.variables;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.InlineVariableRefactoring;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

public class InlineVariableHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    InlineVariableRefactoring ref = (InlineVariableRefactoring) refactoring;
    return info.setGroup(Group.VARIABLE)
        .setNameBefore(Utils.calculateSignature(ref.getOperationAfter()))
        .setNameAfter(Utils.calculateSignature(ref.getOperationBefore()))
        .setElementBefore(ref.getVariableDeclaration().getVariableDeclaration().toQualifiedString())
        .setElementAfter(null)
        .addMarking(ref.getVariableDeclaration().codeRange(),
            ref.getInlinedVariableDeclarationCodeRange(), true);


  }
}
