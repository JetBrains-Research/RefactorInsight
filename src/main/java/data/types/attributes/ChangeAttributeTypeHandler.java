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
        .setNameBefore(
            ref.getOriginalAttribute().getVariableName() + " in class "
                + ref.getClassNameBefore().substring(ref.getClassNameBefore().lastIndexOf(".") + 1))
        .setNameAfter(
            ref.getChangedTypeAttribute().getVariableName() + " in class "
                + ref.getClassNameAfter().substring(ref.getClassNameBefore().lastIndexOf(".") + 1))
        .setElementBefore(ref.getOriginalAttribute().toQualifiedString())
        .setElementAfter(ref.getChangedTypeAttribute().toQualifiedString())
        .addMarking(ref.getOriginalAttribute().getType().codeRange(),
            ref.getChangedTypeAttribute().getType().codeRange());
  }
}
