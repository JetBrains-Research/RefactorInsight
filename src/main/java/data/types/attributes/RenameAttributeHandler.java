package data.types.attributes;

import data.Group;
import data.RefactoringInfo;
import data.RefactoringLine;
import data.types.Handler;
import gr.uom.java.xmi.diff.RenameAttributeRefactoring;
import org.refactoringminer.api.Refactoring;

public class RenameAttributeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RenameAttributeRefactoring ref = (RenameAttributeRefactoring) refactoring;

    String classNameBefore = ref.getClassNameBefore();
    String classNameAfter = ref.getClassNameAfter();

    return info.setGroup(Group.ATTRIBUTE)
        .setGroupId(ref.getClassNameAfter() + "." + ref.getRenamedAttribute().getVariableName())
        .addMarking(ref.getOriginalAttribute().codeRange(),
            ref.getRenamedAttribute().codeRange(),
            line -> line.addOffset(ref.getOriginalAttribute().getLocationInfo(),
                ref.getRenamedAttribute().getLocationInfo()),
            RefactoringLine.MarkingOption.NONE, true)
        .setElementBefore(ref.getOriginalAttribute().getVariableDeclaration().toQualifiedString())
        .setElementAfter(ref.getRenamedAttribute().getVariableDeclaration().toQualifiedString())
        .setNameBefore(classNameBefore)
        .setNameAfter(classNameAfter);
  }
}
