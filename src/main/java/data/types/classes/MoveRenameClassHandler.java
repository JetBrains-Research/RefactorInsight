package data.types.classes;

import data.RefactoringInfo;
import data.Scope;
import data.TrueCodeRange;
import data.types.Handler;
import gr.uom.java.xmi.diff.MoveAndRenameClassRefactoring;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class MoveRenameClassHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    MoveAndRenameClassRefactoring ref = (MoveAndRenameClassRefactoring) refactoring;

    return new RefactoringInfo(Scope.CLASS)
        .setType(RefactoringType.MOVE_RENAME_CLASS)
        .setName(ref.getName())
        .setText(ref.toString())
        .setLeftSide(Arrays.asList(new TrueCodeRange(ref.getOriginalClass().codeRange())))
        .setRightSide(Arrays.asList(new TrueCodeRange(ref.getRenamedClass().codeRange())))
        .setNameBefore(ref.getOriginalClassName())
        .setNameAfter(ref.getRenamedClassName());
  }
}
