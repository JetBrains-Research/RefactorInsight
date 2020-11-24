package org.jetbrains.research.refactorinsight.data.types.variables;

import gr.uom.java.xmi.diff.ExtractVariableRefactoring;
import org.jetbrains.research.refactorinsight.adapters.CodeRange;
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
        .addMarking(new CodeRange(ref.getOperationBefore().getBody().getCompositeStatement().codeRange()),
            new CodeRange(ref.getExtractedVariableDeclarationCodeRange()), true);
  }

  @Override
  public RefactoringInfo specify(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring,
                                 RefactoringInfo info) {
    //This kind of refactoring is not supported by kotlinRMiner yet.
    return null;
  }
}
