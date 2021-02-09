package org.jetbrains.research.refactorinsight.data.types.attributes;

import gr.uom.java.xmi.diff.ExtractAttributeRefactoring;
import org.jetbrains.research.refactorinsight.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.refactoringminer.api.Refactoring;

public class ExtractAttributeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ExtractAttributeRefactoring ref = (ExtractAttributeRefactoring) refactoring;
    ref.leftSide().forEach(extraction ->
        info.addMarking(
            new CodeRange(extraction),
            new CodeRange(ref.getExtractedVariableDeclarationCodeRange()),
            true
        ));
    return info.setGroup(Group.ATTRIBUTE)
        .setDetailsBefore(ref.getOriginalClass().getName())
        .setDetailsAfter(ref.getNextClass().getName())
        .setNameBefore(
            ref.getVariableDeclaration().getName() + " : " + ref.getVariableDeclaration().getType())
        .setNameAfter(ref.getVariableDeclaration().getName() + " : "
            + ref.getVariableDeclaration().getType());
  }

  @Override
  public RefactoringInfo specify(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring,
                                 RefactoringInfo info) {
    //This kind of refactoring is not supported by kotlinRMiner yet.
    return null;
  }
}
