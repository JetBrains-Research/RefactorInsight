package data.types.methods;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class ExtractOperationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ExtractOperationRefactoring ref = (ExtractOperationRefactoring) refactoring;

    //TODO three side view
    if (ref.getRefactoringType() == RefactoringType.EXTRACT_AND_MOVE_OPERATION) {
      return info
          .setElementBefore(ref.getSourceOperationBeforeExtraction().getName() + " in class "
              + ref.getSourceOperationBeforeExtraction().getClassName())
          .setElementAfter("extracted " + ref.getExtractedOperation().getName() + " & moved in "
              + ref.getExtractedOperation().getClassName())
          .setNameBefore(calculateSignature(ref.getSourceOperationBeforeExtraction()))
          .setNameAfter(calculateSignature(ref.getSourceOperationAfterExtraction()));
    } else {
      info.setGroup(Group.METHOD)
          .setElementBefore("from " + ref.getSourceOperationBeforeExtraction().getName())
          .setElementAfter("extracted " + ref.getExtractedOperation().getName())
          .setNameBefore(calculateSignature(ref.getSourceOperationBeforeExtraction()))
          .setNameAfter(calculateSignature(ref.getSourceOperationAfterExtraction()))
          .addMarking(ref.getExtractedCodeRangeFromSourceOperation(),
              ref.getExtractedCodeRangeToExtractedOperation());

      ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
          info.addMarking(ref.getExtractedCodeRangeFromSourceOperation().getStartLine(),
              ref.getExtractedCodeRangeFromSourceOperation().getEndLine(),
              invocation.getStartLine(), invocation.getEndLine(),
              ref.getExtractedCodeRangeFromSourceOperation().getFilePath(),
              invocation.getFilePath())
      );
      return info;
    }
  }
}
