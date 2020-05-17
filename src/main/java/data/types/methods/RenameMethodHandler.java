package data.types.methods;

import data.RefactoringInfo;
import data.TrueCodeRange;
import data.Type;
import data.types.Handler;
import gr.uom.java.xmi.diff.RenameOperationRefactoring;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class RenameMethodHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring, String commitId) {
    RenameOperationRefactoring ref = (RenameOperationRefactoring) refactoring;
    return new RefactoringInfo(Type.METHOD)
        .setType(RefactoringType.RENAME_METHOD)
        .setText(ref.toString())
        .setName(ref.getName())
        .setCommitId(commitId)
        .setLeftSide(
            Arrays.asList(new TrueCodeRange(ref.getOriginalOperation().codeRange())))
        .setRightSide(
            Arrays.asList(new TrueCodeRange(ref.getRenamedOperation().codeRange())))
        .setNameBefore(Handler.calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(Handler.calculateSignature(ref.getRenamedOperation()));
  }
}
