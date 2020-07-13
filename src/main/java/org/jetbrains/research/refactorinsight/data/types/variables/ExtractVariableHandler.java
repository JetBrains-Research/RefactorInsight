package org.jetbrains.research.refactorinsight.data.types.variables;

import gr.uom.java.xmi.diff.ExtractVariableRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.jetbrains.research.refactorinsight.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

public class ExtractVariableHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ExtractVariableRefactoring ref = (ExtractVariableRefactoring) refactoring;

    return info.setGroup(Group.VARIABLE)
        .setNameBefore(StringUtils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(StringUtils.calculateSignature(ref.getOperationAfter()))
        .setElementBefore(ref.getVariableDeclaration().getVariableDeclaration().toQualifiedString())
        .setElementAfter(null)
        .addMarking(ref.getOperationBefore().getBody().getCompositeStatement().codeRange(),
            ref.getExtractedVariableDeclarationCodeRange(), true);
  }
}
