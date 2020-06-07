package data.types.attributes;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.MoveAttributeRefactoring;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

public class MoveAttributeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    MoveAttributeRefactoring ref = (MoveAttributeRefactoring) refactoring;

    String classNameBefore = ref.getSourceClassName();
    String classNameAfter = ref.getTargetClassName();

    return info.setGroup(Group.ATTRIBUTE)
        .setNameBefore(classNameBefore)
        .setNameAfter(classNameAfter)
        .setElementBefore(ref.getOriginalAttribute().getVariableDeclaration().toQualifiedString())
        .setElementAfter(null)
        .addMarking(ref.getOriginalAttribute().codeRange(),
            ref.getMovedAttribute().codeRange(), true);
  }
}
