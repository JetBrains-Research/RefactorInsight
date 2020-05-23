package data.types.methods;

import data.RefactoringInfo;
import data.Scope;
import data.TrueCodeRange;
import data.types.Handler;
import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class ExtractOperationHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    ExtractOperationRefactoring ref = (ExtractOperationRefactoring) refactoring;

    TrueCodeRange left = new TrueCodeRange(ref.getSourceOperationBeforeExtraction().codeRange());
    TrueCodeRange right1 = new TrueCodeRange(ref.getSourceOperationAfterExtraction().codeRange());
    TrueCodeRange right2 = new TrueCodeRange(ref.getExtractedOperationCodeRange());

    if (!ref.getSourceOperationBeforeExtraction().getClassName().equals(ref
        .getExtractedOperation().getClassName())) {
      return new RefactoringInfo(Scope.METHOD)
          .setType(RefactoringType.EXTRACT_AND_MOVE_OPERATION)
          .setName(ref.getName())
          .setText(ref.toString())
          .setElementBefore(ref.getSourceOperationBeforeExtraction().getName() + " in class "
              + ref.getSourceOperationBeforeExtraction().getClassName())
          .setElementAfter("extracted " + ref.getExtractedOperation().getName() + " moved in "
              + ref.getExtractedOperation().getClassName())
          .setLeftSide(Arrays.asList(left))
          .setRightSide(Arrays.asList(right1, right2))
          .setNameBefore(Handler.calculateSignature(ref.getSourceOperationBeforeExtraction()))
          .setNameAfter(Handler.calculateSignature(ref.getSourceOperationAfterExtraction()));
    }
    return new RefactoringInfo(Scope.METHOD)
        .setType(RefactoringType.EXTRACT_OPERATION)
        .setName(ref.getName())
        .setText(ref.toString())
        .setElementBefore("from " + ref.getSourceOperationBeforeExtraction().getName())
        .setElementAfter("extracted " + ref.getExtractedOperation().getName())
        .setLeftSide(Arrays.asList(left))
        .setRightSide(Arrays.asList(right1, right2))
        .setNameBefore(Handler.calculateSignature(ref.getSourceOperationBeforeExtraction()))
        .setNameAfter(Handler.calculateSignature(ref.getSourceOperationAfterExtraction()));
  }
}
