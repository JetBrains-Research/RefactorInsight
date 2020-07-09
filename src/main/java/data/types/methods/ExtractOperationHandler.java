package data.types.methods;

import data.Group;
import data.RefactoringInfo;
import data.RefactoringLine;
import data.types.Handler;
import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;
import utils.StringUtils;

public class ExtractOperationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ExtractOperationRefactoring ref = (ExtractOperationRefactoring) refactoring;

    String classNameBefore = ref.getSourceOperationBeforeExtraction().getClassName();
    String classNameAfter = ref.getExtractedOperation().getClassName();

    String extractedMethod = StringUtils
            .calculateSignatureWithoutClassName(ref.getExtractedOperation());

    if (ref.getRefactoringType() == RefactoringType.EXTRACT_AND_MOVE_OPERATION) {
      info.setGroup(Group.METHOD)
          .setThreeSided(true)
          .setDetailsBefore(classNameBefore)
          .setDetailsAfter(classNameAfter)
          .setElementBefore(extractedMethod)
          .setElementAfter(null)
          .setNameBefore(StringUtils.calculateSignature(ref.getSourceOperationBeforeExtraction()))
          .setNameAfter(StringUtils.calculateSignature(ref.getSourceOperationAfterExtraction()))
          .addMarking(ref.getExtractedCodeRangeFromSourceOperation(),
              ref.getExtractedCodeRangeToExtractedOperation(),
              ref.getExtractedCodeRangeFromSourceOperation(),
              RefactoringLine.VisualisationType.LEFT,
              null,
              RefactoringLine.MarkingOption.NONE,
              true);

      ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
          info.addMarking(
              ref.getSourceOperationCodeRangeBeforeExtraction(),
              ref.getExtractedOperation().getBody().getCompositeStatement().codeRange(),
              invocation,
              RefactoringLine.VisualisationType.RIGHT,
              refactoringLine -> {
                refactoringLine.setWord(new String[] {
                    null,
                    ref.getExtractedOperation().getName(),
                    null
                });
              },
              RefactoringLine.MarkingOption.EXTRACT,
              true));
      return info;
    } else {
      info.setGroup(Group.METHOD)
          .setDetailsBefore(classNameBefore)
          .setDetailsAfter(classNameAfter)
          .setElementBefore(extractedMethod)
          .setElementAfter(null)
          .setNameBefore(StringUtils.calculateSignature(ref.getSourceOperationBeforeExtraction()))
          .setNameAfter(StringUtils.calculateSignature(ref.getSourceOperationAfterExtraction()))
              .addMarking(ref.getSourceOperationCodeRangeBeforeExtraction(),
                      ref.getSourceOperationCodeRangeAfterExtraction(),
                      false)
          .addMarking(ref.getExtractedCodeRangeFromSourceOperation(),
              ref.getExtractedCodeRangeToExtractedOperation(),
              true);

      ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
          info.addMarking(
              ref.getExtractedCodeRangeFromSourceOperation(),
              invocation,
              null,
              RefactoringLine.MarkingOption.ADD,
              true)
      );
      return info;
    }
  }
}
