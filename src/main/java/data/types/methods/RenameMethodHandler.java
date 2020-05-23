package data.types.methods;

import data.RefactoringInfo;
import data.Scope;
import data.TrueCodeRange;
import data.types.Handler;
import gr.uom.java.xmi.diff.RenameOperationRefactoring;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class RenameMethodHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    RenameOperationRefactoring ref = (RenameOperationRefactoring) refactoring;
    return new RefactoringInfo(Scope.METHOD)
        .setType(RefactoringType.RENAME_METHOD)
        .setText(ref.toString())
        .setName(ref.getName())
        .setElementBefore(ref.getOriginalOperation().getName())
        .setElementAfter(ref.getRenamedOperation().getName())
        .setLeftSide(
            Arrays.asList(new TrueCodeRange(ref.getOriginalOperation().codeRange())))
        .setRightSide(
            Arrays.asList(new TrueCodeRange(ref.getRenamedOperation().codeRange())))
        .setNameBefore(Handler.calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(Handler.calculateSignature(ref.getRenamedOperation()));
  }
}
