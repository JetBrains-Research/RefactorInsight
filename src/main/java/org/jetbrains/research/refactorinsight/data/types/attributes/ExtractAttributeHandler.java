package org.jetbrains.research.refactorinsight.data.types.attributes;

import gr.uom.java.xmi.diff.ExtractAttributeRefactoring;
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
            extraction,
            ref.getExtractedVariableDeclarationCodeRange(),
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
}
