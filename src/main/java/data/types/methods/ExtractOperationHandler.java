package data.types.methods;

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

    String classNameBefore = ref.getSourceOperationBeforeExtraction().getClassName();
    String classNameAfter = ref.getExtractedOperation().getClassName();

    String extractedMethod = Utils.calculateSignature(ref.getExtractedOperation());

    if (ref.getRefactoringType() == RefactoringType.EXTRACT_AND_MOVE_OPERATION) {
      info.setGroup(RefactoringInfo.Group.METHOD)
          .setThreeSided(true)
          .setDetailsBefore(classNameBefore)
          .setDetailsAfter(classNameAfter)
          .setElementBefore(extractedMethod.substring(extractedMethod.lastIndexOf(".") + 1))
          .setElementAfter(null)
          .setNameBefore(Utils.calculateSignature(ref.getSourceOperationBeforeExtraction()))
          .setNameAfter(Utils.calculateSignature(ref.getSourceOperationAfterExtraction()))
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
                refactoringLine.setLazilyHighlightableWords(new String[] {
                    null,
                    ref.getExtractedOperation().getName(),
                    null
                });
              },
              RefactoringLine.MarkingOption.EXTRACT,
              true));
      return info;
    } else {
      info.setGroup(RefactoringInfo.Group.METHOD)
          .setDetailsBefore(classNameBefore)
          .setDetailsAfter(classNameAfter)
          .setElementBefore(extractedMethod.substring(extractedMethod.lastIndexOf(".") + 1))
          .setElementAfter(null)
          .setNameBefore(Utils.calculateSignature(ref.getSourceOperationBeforeExtraction()))
          .setNameAfter(Utils.calculateSignature(ref.getSourceOperationAfterExtraction()))
          .addMarking(ref.getExtractedCodeRangeFromSourceOperation(),
              ref.getExtractedCodeRangeToExtractedOperation(),
              true);

      ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
          info.addMarking(
              ref.getExtractedCodeRangeFromSourceOperation(),
              invocation,
              null,
              RefactoringLine.MarkingOption.ADD,
              false)
      );
      return info;
    }
  }
}
