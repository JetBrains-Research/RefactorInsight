package org.jetbrains.research.refactorinsight.data.types.methods;

import gr.uom.java.xmi.diff.ChangeReturnTypeRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.jetbrains.research.refactorinsight.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

public class ChangeReturnTypeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ChangeReturnTypeRefactoring ref = (ChangeReturnTypeRefactoring) refactoring;
    if (ref.getOperationAfter().isGetter()
        && !ref.getOperationAfter().getBody().getAllVariables().isEmpty()) {
      String id = ref.getOperationAfter().getClassName() + "."
          + ref.getOperationAfter().getBody().getAllVariables().get(0);
      info.setGroupId(id);
    }

    String classNameBefore = ref.getOperationBefore().getClassName();
    String classNameAfter = ref.getOperationAfter().getClassName();

    return info.setGroup(Group.METHOD)
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
