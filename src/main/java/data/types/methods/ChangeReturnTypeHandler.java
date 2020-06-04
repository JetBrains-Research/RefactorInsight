package data.types.methods;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ChangeReturnTypeRefactoring;
import org.refactoringminer.api.Refactoring;

public class ChangeReturnTypeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    ChangeReturnTypeRefactoring ref = (ChangeReturnTypeRefactoring) refactoring;
    if (ref.getOperationAfter().isGetter()) {
      String id = ref.getOperationAfter().getClassName() + "."
          + ref.getOperationAfter().getBody().getAllVariables().get(0);
      info.setGroupId(id);
    }
    return info.setGroup(Group.METHOD)
        .setElementBefore(ref.getOriginalType().toString())
        .setElementAfter(ref.getChangedType().toString())
        .setNameBefore(calculateSignature(ref.getOperationBefore()))
        .setNameAfter(calculateSignature(ref.getOperationBefore()))
        .addMarking(ref.getOriginalType().codeRange(), ref.getChangedType().codeRange());

  }

}
