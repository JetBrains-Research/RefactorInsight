package data.types.attributes;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.MoveAndRenameAttributeRefactoring;
import org.refactoringminer.api.Refactoring;

public class MoveRenameAttributeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    MoveAndRenameAttributeRefactoring ref = (MoveAndRenameAttributeRefactoring) refactoring;
    return info.setGroup(Group.ATTRIBUTE)
        .setElementBefore(ref.getSourceClassName())
        .setElementAfter(ref.getTargetClassName())
        .setNameBefore(ref.getOriginalAttribute().getName())
        .setNameAfter(ref.getMovedAttribute().getName())
        .addMarking(ref.getSourceAttributeCodeRangeBeforeMove(),
            ref.getTargetAttributeCodeRangeAfterMove());

  }
}
