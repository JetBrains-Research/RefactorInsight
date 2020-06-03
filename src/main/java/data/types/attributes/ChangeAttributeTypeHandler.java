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
        .setGroupId(ref.getClassNameAfter() + "." + ref.getChangedTypeAttribute().getVariableName())
        .setElementBefore(ref.getOriginalAttribute().toQualifiedString())
        .setElementAfter(ref.getChangedTypeAttribute().toQualifiedString())
        .setNameBefore("in class " + ref.getClassNameAfter()
            .substring(ref.getClassNameBefore().lastIndexOf(".") + 1))
        .setNameAfter("in class " + ref.getClassNameAfter()
            .substring(ref.getClassNameBefore().lastIndexOf(".") + 1))
        .addMarking(ref.getOriginalAttribute().codeRange(),
            ref.getChangedTypeAttribute().codeRange(), line ->
                line.addOffset(ref.getOriginalAttribute().getType().getLocationInfo(),
                    ref.getChangedTypeAttribute().getType().getLocationInfo()));

  }
}
