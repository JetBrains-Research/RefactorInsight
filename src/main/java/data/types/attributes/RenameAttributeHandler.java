package data.types.attributes;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.RenameAttributeRefactoring;
import org.refactoringminer.api.Refactoring;

public class RenameAttributeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RenameAttributeRefactoring ref = (RenameAttributeRefactoring) refactoring;
    return info.setGroup(Group.ATTRIBUTE)
        .setElementBefore(null)
        .setElementAfter(null)
        .setNameBefore(ref.getRenamedAttribute().getVariableName())
        .setNameAfter(ref.getRenamedAttribute().getVariableName())
        .addMarking(ref.getOriginalAttribute().codeRange(), ref.getRenamedAttribute().codeRange());

  }
}
