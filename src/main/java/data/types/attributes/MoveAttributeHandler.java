package data.types.attributes;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.MoveAttributeRefactoring;
import org.refactoringminer.api.Refactoring;

public class MoveAttributeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    MoveAttributeRefactoring ref = (MoveAttributeRefactoring) refactoring;

    String classNameBefore = ref.getSourceClassName();
    String classNameAfter = ref.getTargetClassName();

    return info.setGroup(Group.ATTRIBUTE)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setNameBefore(ref.getOriginalAttribute().getVariableDeclaration().toQualifiedString())
        .setNameAfter(ref.getMovedAttribute().getVariableDeclaration().toQualifiedString())
        .addMarking(ref.getOriginalAttribute().codeRange(),
            ref.getMovedAttribute().codeRange(), true);
  }
}
