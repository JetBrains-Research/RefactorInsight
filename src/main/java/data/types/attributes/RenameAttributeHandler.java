package data.types.attributes;

import data.RefactoringInfo;
import data.TrueCodeRange;
import data.Type;
import data.types.Handler;
import gr.uom.java.xmi.diff.RenameAttributeRefactoring;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class RenameAttributeHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    RenameAttributeRefactoring ref = (RenameAttributeRefactoring) refactoring;
    return new RefactoringInfo(Type.ATTRIBUTE)
        .setType(RefactoringType.RENAME_ATTRIBUTE)
        .setName(ref.getName())
        .setText(ref.toString())
        .setNameBefore(ref.getOriginalAttribute().getVariableName())
        .setNameAfter(ref.getRenamedAttribute().getVariableName())
        .setLeftSide(Arrays.asList(new TrueCodeRange(ref.getOriginalAttribute().codeRange())))
        .setRightSide(Arrays.asList(new TrueCodeRange(ref.getRenamedAttribute().codeRange())));
  }
}
