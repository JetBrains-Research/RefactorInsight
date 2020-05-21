package data.types.attributes;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.PullUpAttributeRefactoring;
import org.refactoringminer.api.Refactoring;

public class PullUpAttributeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    PullUpAttributeRefactoring ref = (PullUpAttributeRefactoring) refactoring;
    return info.setGroup(Group.ATTRIBUTE).addMarking(ref.getSourceAttributeCodeRangeBeforeMove(),
        ref.getTargetAttributeCodeRangeAfterMove());
  }
}
