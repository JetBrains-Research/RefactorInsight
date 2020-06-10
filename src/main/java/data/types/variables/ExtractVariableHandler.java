package data.types.variables;

import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ExtractVariableRefactoring;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

public class ExtractVariableHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ExtractVariableRefactoring ref = (ExtractVariableRefactoring) refactoring;
    return info.setGroup(RefactoringInfo.Group.VARIABLE)
        .setNameBefore(Utils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(Utils.calculateSignature(ref.getOperationAfter()))
        .setElementBefore(ref.getVariableDeclaration().getVariableDeclaration().toQualifiedString())
        .setElementAfter(null)
        .addMarking(ref.getOperationBefore().codeRange(),
            ref.getExtractedVariableDeclarationCodeRange(), true);
  }
}
