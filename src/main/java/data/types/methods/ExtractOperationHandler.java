package data.types.methods;

import data.RefactoringEntry;
import data.RefactoringInfo;
import data.TrueCodeRange;
import data.Type;
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
      return new RefactoringInfo(Type.METHOD)
          .setType(RefactoringType.EXTRACT_AND_MOVE_OPERATION)
          .setName(ref.getName())
          .setText(ref.toString())
          .setLeftSide(Arrays.asList(left))
          .setRightSide(Arrays.asList(right1, right2))
          .setNameBefore(Handler.calculateSignature(ref.getSourceOperationBeforeExtraction()))
          .setNameAfter(Handler.calculateSignature(ref.getSourceOperationAfterExtraction()));
    }
    return new RefactoringInfo(Type.METHOD)
        .setType(RefactoringType.EXTRACT_OPERATION)
        .setName(ref.getName())
        .setText(ref.toString())
        .setLeftSide(Arrays.asList(left))
        .setRightSide(Arrays.asList(right1, right2))
        .setNameBefore(Handler.calculateSignature(ref.getSourceOperationBeforeExtraction()))
        .setNameAfter(Handler.calculateSignature(ref.getSourceOperationAfterExtraction()));
  }
}
