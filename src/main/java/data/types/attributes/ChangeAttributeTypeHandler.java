package data.types.attributes;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ChangeAttributeTypeRefactoring;
import org.refactoringminer.api.Refactoring;

public class ChangeAttributeTypeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ChangeAttributeTypeRefactoring ref = (ChangeAttributeTypeRefactoring) refactoring;
    return info.setGroup(Group.ATTRIBUTE)
        .setElementBefore(null)
        .setElementAfter(null)
        .setNameBefore(ref.getOriginalAttribute().toQualifiedString())
        .setNameAfter(ref.getChangedTypeAttribute().toQualifiedString())
        .addMarking(ref.getOriginalAttribute().codeRange(),
            ref.getChangedTypeAttribute().codeRange()).addMarking(ref.getOriginalAttribute().codeRange(),
            ref.getChangedTypeAttribute().codeRange(), line ->
                line.addOffset(ref.getOriginalAttribute().getType().getLocationInfo(),
                    ref.getChangedTypeAttribute().getType().getLocationInfo()));;

  }
}
