package data.types.attributes;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.MoveAttributeRefactoring;
import org.refactoringminer.api.Refactoring;

public class MoveAttributeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    MoveAttributeRefactoring ref = (MoveAttributeRefactoring) refactoring;

    return info.setGroup(Group.ATTRIBUTE)
        .setElementBefore(ref.getSourceClassName())
        .setElementAfter(ref.getTargetClassName())
        .setNameBefore(ref.getOriginalAttribute().toQualifiedString())
        .setNameAfter(ref.getOriginalAttribute().toQualifiedString())
        .addMarking(ref.getOriginalAttribute().codeRange(),
            ref.getMovedAttribute().codeRange(), true);
  }
}
