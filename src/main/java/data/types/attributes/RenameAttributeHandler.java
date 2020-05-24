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
        .addMarking(ref.getOriginalAttribute().codeRange(), ref.getRenamedAttribute().codeRange(),
            line -> line.addOffset(ref.getOriginalAttribute().getLocationInfo(),
                ref.getRenamedAttribute().getLocationInfo()))
        .setNameBefore(
            ref.getOriginalAttribute().getVariableName() + " in class "
                + ref.getClassNameBefore().substring(ref.getClassNameBefore().lastIndexOf(".") + 1))
        .setNameAfter(
            ref.getOriginalAttribute().getVariableName() + " in class "
                + ref.getClassNameBefore().substring(ref.getClassNameBefore().lastIndexOf(".") + 1))
        .setElementBefore(ref.getOriginalAttribute().getVariableName())
        .setElementAfter(ref.getRenamedAttribute().getVariableName());
  }
}
