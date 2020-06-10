package data.types.attributes;

import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ExtractAttributeRefactoring;
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
    return info.setGroup(RefactoringInfo.Group.ATTRIBUTE)
        .setDetailsBefore(ref.getOriginalClass().getName())
        .setDetailsAfter(ref.getOriginalClass().getName())
        .setNameBefore(
            ref.getVariableDeclaration().getName() + " : " + ref.getVariableDeclaration().getType())
        .setNameAfter(ref.getVariableDeclaration().getName() + " : "
            + ref.getVariableDeclaration().getType());
  }
}
