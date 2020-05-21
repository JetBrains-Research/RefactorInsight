package data.types.attributes;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.PushDownAttributeRefactoring;
import org.refactoringminer.api.Refactoring;

public class PushDownAttributeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    PushDownAttributeRefactoring ref = (PushDownAttributeRefactoring) refactoring;
    return info.setGroup(Group.ATTRIBUTE)
        .addMarking(ref.getSourceAttributeCodeRangeBeforeMove(),
            ref.getTargetAttributeCodeRangeAfterMove());
  }
}
