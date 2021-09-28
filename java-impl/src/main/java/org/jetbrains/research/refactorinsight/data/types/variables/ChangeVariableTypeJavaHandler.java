package org.jetbrains.research.refactorinsight.data.types.variables;

import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.VariableDeclarationContainer;
import gr.uom.java.xmi.diff.ChangeVariableTypeRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

public class ChangeVariableTypeJavaHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ChangeVariableTypeRefactoring ref = (ChangeVariableTypeRefactoring) refactoring;
    final UMLOperation operationAfter = (UMLOperation) ref.getOperationAfter();
    String id = operationAfter.getClassName() + ".";
    if ((operationAfter.isSetter() || operationAfter.isConstructor())
        && ref.getChangedTypeVariable().isParameter()) {
      id += ref.getChangedTypeVariable().getVariableName();
    } else {
      id = StringUtils.calculateSignature(ref.getOperationAfter()) + "."
          + ref.getChangedTypeVariable().getVariableName();
    }
    info.setGroupId(id);
    if (ref.getChangedTypeVariable().isParameter()) {
      info.setGroup(Group.METHOD)
          .setDetailsBefore(ref.getOperationBefore().getClassName())
          .setDetailsAfter(ref.getOperationAfter().getClassName());
    } else {
      info.setGroup(Group.VARIABLE);
    }

    return info
        .setNameBefore(StringUtils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(StringUtils.calculateSignature(ref.getOperationAfter()))
        .setElementBefore(ref.getOriginalVariable().getVariableDeclaration().toQualifiedString())
        .setElementAfter(ref.getChangedTypeVariable().getVariableDeclaration().toQualifiedString())
        .addMarking(new CodeRange(ref.getOriginalVariable().getType().codeRange()),
            new CodeRange(ref.getChangedTypeVariable().getType().codeRange()), true);

  }

}
