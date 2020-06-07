package data.types.attributes;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ReplaceAttributeRefactoring;
import org.refactoringminer.api.Refactoring;

public class ReplaceAttributeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ReplaceAttributeRefactoring ref = (ReplaceAttributeRefactoring) refactoring;

    String classNameBefore = ref.getSourceClassName();
    String classNameAfter = ref.getTargetClassName();
    return info.setGroup(Group.ATTRIBUTE)
        .setNameBefore(classNameBefore)
        .setNameAfter(classNameAfter)
        .setElementBefore(ref.getOriginalAttribute().getVariableDeclaration().toQualifiedString())
        .setElementAfter(ref.getMovedAttribute().getVariableDeclaration().toQualifiedString())
        .addMarking(ref.getSourceAttributeCodeRangeBeforeMove(),
            ref.getTargetAttributeCodeRangeAfterMove(), true);
  }
}
