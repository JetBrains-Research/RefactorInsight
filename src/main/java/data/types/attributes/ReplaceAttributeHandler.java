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
    return info.setGroup(Group.ATTRIBUTE)
        .setElementBefore(ref.getSourceClassName())
        .setElementAfter(ref.getTargetClassName())
        .setNameBefore(ref.getOriginalAttribute().toQualifiedString())
        .setNameAfter(ref.getMovedAttribute().toQualifiedString())
        .addMarking(ref.getSourceAttributeCodeRangeBeforeMove(),
            ref.getTargetAttributeCodeRangeAfterMove());
  }
}
