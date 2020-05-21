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
    return info.setGroup(Group.ATTRIBUTE).addMarking(ref.getSourceAttributeCodeRangeBeforeMove(),
        ref.getTargetAttributeCodeRangeAfterMove());
  }
}
