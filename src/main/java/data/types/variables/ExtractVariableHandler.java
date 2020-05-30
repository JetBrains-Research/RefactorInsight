package data.types.variables;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ExtractVariableRefactoring;
import org.refactoringminer.api.Refactoring;

public class ExtractVariableHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ExtractVariableRefactoring ref = (ExtractVariableRefactoring) refactoring;
    return info.setGroup(Group.VARIABLE)
        .setElementBefore("in method " + ref.getOperationBefore().getName())
        .setElementAfter(null)
        .setNameBefore(ref.getVariableDeclaration().getVariableName())
        .setNameAfter(ref.getVariableDeclaration().getVariableName())
        .addMarking(ref.getOperationBefore().codeRange(),
            ref.getExtractedVariableDeclarationCodeRange());
  }
}
