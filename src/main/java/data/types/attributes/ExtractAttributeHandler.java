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
    return info.setGroup(Group.ATTRIBUTE).
        setNameBefore(ref.getOriginalClass().getName())
        .setNameAfter(ref.getNextClass().getName())
        .addMarking(ref.getVariableDeclaration().codeRange(),
            ref.getExtractedVariableDeclarationCodeRange());

  }
}
