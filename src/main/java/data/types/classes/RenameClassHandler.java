package data.types.classes;

import data.RefactoringInfo;
import data.TrueCodeRange;
import data.Type;
import data.types.Handler;
import gr.uom.java.xmi.diff.RenameClassRefactoring;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class RenameClassHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring, String commitId) {
    RenameClassRefactoring ref = (RenameClassRefactoring) refactoring;

    return new RefactoringInfo(Type.CLASS)
        .setType(RefactoringType.RENAME_CLASS)
        .setName(ref.getName())
        .setText(ref.toString())
        .setCommitId(commitId)
        .setLeftSide(Arrays.asList(new TrueCodeRange(ref.getOriginalClass().codeRange())))
        .setRightSide(Arrays.asList(new TrueCodeRange(ref.getRenamedClass().codeRange())))
        .setNameBefore(ref.getOriginalClassName())
        .setNameAfter(ref.getRenamedClassName());
  }
}
