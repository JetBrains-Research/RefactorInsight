package data.types.methods;

import data.RefactoringInfo;
import data.Scope;
import data.TrueCodeRange;
import data.types.Handler;
import gr.uom.java.xmi.diff.CodeRange;
import gr.uom.java.xmi.diff.InlineOperationRefactoring;
import java.util.ArrayList;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class InlineOperationHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    InlineOperationRefactoring ref = (InlineOperationRefactoring) refactoring;
    TrueCodeRange left = new TrueCodeRange(ref.getTargetOperationBeforeInline().codeRange());
    ArrayList<TrueCodeRange> right = new ArrayList<>();
    for (CodeRange codeRange : ref.getInlinedOperationInvocationCodeRanges()) {
      right.add(new TrueCodeRange(codeRange));
    }

    if (!ref.getTargetOperationBeforeInline().getClassName().equals(ref
        .getTargetOperationAfterInline().getClassName())) {
      return new RefactoringInfo(Scope.METHOD)
          .setType(RefactoringType.MOVE_AND_INLINE_OPERATION)
          .setName(ref.getName())
          .setText(ref.toString())
          .setLeftSide(Arrays.asList(left))
          .setRightSide(right)
          .setElementBefore(ref.getInlinedOperation().getName())
          .setElementAfter(" in method " + ref.getTargetOperationAfterInline().getName())
          .setNameBefore(Handler.calculateSignature(ref.getTargetOperationBeforeInline()))
          .setNameAfter(Handler.calculateSignature(ref.getTargetOperationAfterInline()));
    }

    return new RefactoringInfo(Scope.METHOD)
        .setType(RefactoringType.INLINE_OPERATION)
        .setName(ref.getName())
        .setText(ref.toString())
        .setElementBefore(ref.getInlinedOperation().getName())
        .setElementAfter(" in method " + ref.getTargetOperationAfterInline().getName())
        .setLeftSide(Arrays.asList(left))
        .setRightSide(right)
        .setNameBefore(Handler.calculateSignature(ref.getTargetOperationBeforeInline()))
        .setNameAfter(Handler.calculateSignature(ref.getTargetOperationAfterInline()));
  }
}
