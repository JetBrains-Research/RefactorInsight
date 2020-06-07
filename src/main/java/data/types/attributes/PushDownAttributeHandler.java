package data.types.attributes;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.PushDownAttributeRefactoring;
import org.refactoringminer.api.Refactoring;

public class PushDownAttributeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    PushDownAttributeRefactoring ref = (PushDownAttributeRefactoring) refactoring;
    return info.setGroup(Group.ATTRIBUTE)
        .setElementBefore(ref.getSourceClassName())
        .setElementAfter(ref.getTargetClassName())
        .setNameBefore(ref.getOriginalAttribute().toQualifiedString())
        .setNameAfter(ref.getMovedAttribute().toQualifiedString())
        .addMarking(ref.getSourceAttributeCodeRangeBeforeMove(),
            ref.getTargetAttributeCodeRangeAfterMove(), true);
  }
}
