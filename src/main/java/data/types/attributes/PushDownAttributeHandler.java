package data.types.attributes;

import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.PushDownAttributeRefactoring;
import org.refactoringminer.api.Refactoring;

public class PushDownAttributeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    PushDownAttributeRefactoring ref = (PushDownAttributeRefactoring) refactoring;

    String classNameBefore = ref.getSourceClassName();
    String classNameAfter = ref.getTargetClassName();

    return info.setGroup(RefactoringInfo.Group.ATTRIBUTE)
        .setNameBefore(classNameBefore)
        .setNameAfter(classNameAfter)
        .setElementBefore(ref.getOriginalAttribute().getVariableDeclaration().toQualifiedString())
        .setElementAfter(null)
        .addMarking(ref.getSourceAttributeCodeRangeBeforeMove(),
            ref.getTargetAttributeCodeRangeAfterMove(), true);
  }
}
