package data.types.methods;

import data.Group;
import data.RefactoringInfo;
import data.RefactoringLine;
import data.types.Handler;
import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;
import utils.Utils;

public class ExtractOperationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ExtractOperationRefactoring ref = (ExtractOperationRefactoring) refactoring;
    String classBefore = ref.getSourceOperationBeforeExtraction().getClassName();
    String classAfter = ref.getExtractedOperation().getClassName();
    int index = Utils.indexOfDifference(classBefore, classAfter);
    if (ref.getRefactoringType() == RefactoringType.EXTRACT_AND_MOVE_OPERATION) {
      info.setGroup(Group.METHOD)
          .setThreeSided(true)
          .setElementBefore(ref.getSourceOperationBeforeExtraction().getName() + " in class "
              + classBefore.substring(index))
          .setElementAfter("extracted " + ref.getExtractedOperation().getName() + " & moved in "
              + classAfter.substring(index))
          .setNameBefore(calculateSignature(ref.getSourceOperationBeforeExtraction()))
          .setNameAfter(calculateSignature(ref.getSourceOperationAfterExtraction()))
          .addMarking(ref.getExtractedCodeRangeFromSourceOperation(),
              ref.getExtractedCodeRangeToExtractedOperation(),
              ref.getExtractedCodeRangeFromSourceOperation(),
              RefactoringLine.ThreeSidedType.LEFT);

      ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
          info.addMarking(1, 1, ref.getExtractedOperationCodeRange().getStartLine(),
              ref.getExtractedOperationCodeRange().getStartLine(), invocation.getStartLine(),
              invocation.getEndLine(),
              ref.getSourceOperationCodeRangeBeforeExtraction().getFilePath(),
              ref.getExtractedOperationCodeRange().getFilePath(),
              invocation.getFilePath(), RefactoringLine.ThreeSidedType.RIGHT));
      return info;
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
              ref.getExtractedCodeRangeFromSourceOperation().getStartLine() - 1,
              invocation.getStartLine(), invocation.getEndLine(),
              ref.getExtractedCodeRangeFromSourceOperation().getFilePath(),
              invocation.getFilePath())
      );
      return info;
    }
  }
}
