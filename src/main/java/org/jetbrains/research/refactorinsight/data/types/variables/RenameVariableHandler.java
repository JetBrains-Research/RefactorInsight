package org.jetbrains.research.refactorinsight.data.types.variables;

import gr.uom.java.xmi.diff.RenameVariableRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.jetbrains.research.refactorinsight.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

public class RenameVariableHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RenameVariableRefactoring ref = (RenameVariableRefactoring) refactoring;
    String id = ref.getOperationAfter().getClassName() + ".";
    if ((ref.getOperationAfter().isConstructor() || ref.getOperationAfter().isSetter())
        && ref.getRenamedVariable().isParameter()) {
      id += ref.getRenamedVariable().getVariableName();
    } else {
      id = StringUtils.calculateSignature(ref.getOperationAfter()) + "."
          + ref.getRenamedVariable().getVariableName();
    }
    info.setGroupId(id);

    if (ref.getRenamedVariable().isParameter()) {
      info.setGroup(Group.METHOD)
          .setDetailsBefore(ref.getOperationBefore().getClassName())
          .setDetailsAfter(ref.getOperationAfter().getClassName());
    } else {
      info.setGroup(Group.VARIABLE);
    }

    return info
        .setElementBefore(ref.getOriginalVariable().getVariableDeclaration().toQualifiedString())
        .setElementAfter(ref.getRenamedVariable().getVariableDeclaration().toQualifiedString())
        .setNameBefore(StringUtils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(StringUtils.calculateSignature(ref.getOperationAfter()))
        .addMarking(ref.getOriginalVariable().getVariableDeclaration().codeRange(),
            ref.getRenamedVariable().codeRange(), true);
  }
}
