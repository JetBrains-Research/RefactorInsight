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
        .setNameBefore(ref.getOperationBefore().getClassName())
        .setNameAfter(ref.getOperationAfter().getClassName())
        .addMarking(ref.getVariableDeclaration().codeRange(),
            ref.getExtractedVariableDeclarationCodeRange());
  }
}
