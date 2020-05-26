package data.types.variables;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.RenameVariableRefactoring;
import org.refactoringminer.api.Refactoring;

public class RenameVariableHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RenameVariableRefactoring ref = (RenameVariableRefactoring) refactoring;
    return info.setGroup(Group.VARIABLE)
        .setNameBefore(ref.getOriginalVariable().getVariableName() + " in method "
            + ref.getOperationBefore().getName())
        .setNameAfter(ref.getOriginalVariable().getVariableName() + " in method "
            + ref.getOperationBefore().getName())
        .setElementBefore(ref.getOriginalVariable().getVariableDeclaration().toQualifiedString())
        .setElementAfter(ref.getRenamedVariable().getVariableDeclaration().toQualifiedString())
        .addMarking(ref.getOriginalVariable().codeRange(), ref.getRenamedVariable().codeRange(),
                line -> line.addOffset(ref.getOriginalVariable().getLocationInfo(),
                        ref.getRenamedVariable().getLocationInfo()));
  }
}
