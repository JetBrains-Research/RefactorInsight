package data.types.methods;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.RenameOperationRefactoring;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

public class RenameMethodHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RenameOperationRefactoring ref = (RenameOperationRefactoring) refactoring;
    String id = ref.getRenamedOperation().getClassName() + ".";
    if (ref.getRenamedOperation().isGetter()) {
      id += ref.getRenamedOperation().getBody().getAllVariables().get(0);
      info.setGroupId(id);
    }
    if (ref.getRenamedOperation().isSetter()) {
      id += ref.getRenamedOperation().getParameterNameList().get(0);
      info.setGroupId(id);
    }
    return info.setGroup(Group.METHOD)
        .setElementBefore(null)
        .setElementAfter(null)
        .addMarking(ref.getSourceOperationCodeRangeBeforeRename(),
            ref.getTargetOperationCodeRangeAfterRename())
        .setNameBefore(calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(calculateSignature(ref.getRenamedOperation()));
  }
}
