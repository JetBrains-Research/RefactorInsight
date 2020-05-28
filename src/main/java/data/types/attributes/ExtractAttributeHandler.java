package data.types.attributes;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ExtractAttributeRefactoring;
import org.refactoringminer.api.Refactoring;

public class ExtractAttributeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ExtractAttributeRefactoring ref = (ExtractAttributeRefactoring) refactoring;

    info.setGroup(Group.ATTRIBUTE)
        .setElementBefore(ref.getOriginalClass().getName())
        .setElementAfter(ref.getNextClass().getName())
        .setNameBefore(ref.getVariableDeclaration().getName())
        .setNameAfter(ref.getVariableDeclaration().getName())
        .addMarking(ref.getExtractedVariableDeclarationCodeRange().getStartLine(),
                ref.getExtractedVariableDeclarationCodeRange().getStartLine() - 1,
            ref.getExtractedVariableDeclarationCodeRange().getStartLine(),
                ref.getExtractedVariableDeclarationCodeRange().getEndLine(),
                ref.getOriginalClass().getLocationInfo().getFilePath(),
                ref.getNextClass().getLocationInfo().getFilePath());
    ref.leftSide().forEach(extraction ->
            info.addMarking(extraction.getStartLine(), extraction.getEndLine(),
                    ref.getExtractedVariableDeclarationCodeRange().getStartLine(),
                    ref.getExtractedVariableDeclarationCodeRange().getEndLine(),
                    extraction.getFilePath(), ref.getExtractedVariableDeclarationCodeRange().getFilePath()));
    return info;
  }
}
