package data.types.attributes;

import data.RefactoringInfo;
import data.TrueCodeRange;
import data.Type;
import data.types.Handler;
import gr.uom.java.xmi.diff.MoveAndRenameAttributeRefactoring;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class MoveRenameAttributeHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    MoveAndRenameAttributeRefactoring ref = (MoveAndRenameAttributeRefactoring) refactoring;
    return new RefactoringInfo(Type.ATTRIBUTE)
        .setType(RefactoringType.MOVE_RENAME_ATTRIBUTE)
        .setName(ref.getName())
        .setText(ref.toString())
        .setNameBefore(
            ref.getOriginalAttribute().getName() + " in class " + ref.getSourceClassName())
        .setNameAfter(ref.getMovedAttribute().getName() + " in class " + ref.getTargetClassName())
        .setLeftSide(Arrays.asList(new TrueCodeRange(ref.getOriginalAttribute().codeRange())))
        .setRightSide(Arrays.asList(new TrueCodeRange(ref.getMovedAttribute().codeRange())));
  }
}
