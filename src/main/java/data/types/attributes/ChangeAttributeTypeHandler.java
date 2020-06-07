package data.types.attributes;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ChangeAttributeTypeRefactoring;
import org.refactoringminer.api.Refactoring;

public class ChangeAttributeTypeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    ChangeAttributeTypeRefactoring ref = (ChangeAttributeTypeRefactoring) refactoring;
    return info.setGroup(Group.ATTRIBUTE)
        .setGroupId(ref.getClassNameAfter() + "." + ref.getChangedTypeAttribute().getVariableName())
        .setNameBefore(ref.getOriginalAttribute().toQualifiedString())
        .setNameAfter(ref.getChangedTypeAttribute().toQualifiedString())
        .setElementBefore(null)
        .setElementAfter(null)
        .addMarking(ref.getOriginalAttribute().getType().codeRange(),
            ref.getChangedTypeAttribute().getType().codeRange(),
            true);

  }
}
