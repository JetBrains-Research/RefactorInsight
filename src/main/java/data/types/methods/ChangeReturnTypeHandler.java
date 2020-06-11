package data.types.methods;

import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ChangeReturnTypeRefactoring;
import org.refactoringminer.api.Refactoring;
import utils.StringUtils;

public class ChangeReturnTypeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ChangeReturnTypeRefactoring ref = (ChangeReturnTypeRefactoring) refactoring;
    if (ref.getOperationAfter().isGetter()) {
      String id = ref.getOperationAfter().getClassName() + "."
          + ref.getOperationAfter().getBody().getAllVariables().get(0);
      info.setGroupId(id);
    }

    String classNameBefore = ref.getOperationBefore().getClassName();
    String classNameAfter = ref.getOperationAfter().getClassName();

    return info.setGroup(RefactoringInfo.Group.METHOD)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setElementBefore(ref.getOriginalType().toString())
        .setElementAfter(ref.getChangedType().toString())
        .setNameBefore(StringUtils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(StringUtils.calculateSignature(ref.getOperationAfter()))
        .addMarking(ref.getOriginalType().codeRange(), ref.getChangedType().codeRange(),
            true);

  }

}
