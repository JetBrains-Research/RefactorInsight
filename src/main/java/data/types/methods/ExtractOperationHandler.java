package data.types.methods;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.LocalFilePath;
import com.intellij.openapi.vcs.VcsException;
import data.Group;
import data.RefactoringInfo;
import data.RefactoringLine;
import data.types.Handler;
import git4idea.GitContentRevision;
import git4idea.GitRevisionNumber;
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
          .setNameBefore(Utils.calculateSignature(ref.getSourceOperationBeforeExtraction()))
          .setNameAfter(Utils.calculateSignature(ref.getSourceOperationAfterExtraction()))
          .addMarking(ref.getExtractedCodeRangeFromSourceOperation(),
              ref.getExtractedCodeRangeToExtractedOperation(),
              ref.getExtractedCodeRangeFromSourceOperation(),
              RefactoringLine.VisualisationType.LEFT,
              true);

      ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
          info.addMarking(
              ref.getSourceOperationCodeRangeBeforeExtraction(),
              ref.getExtractedOperation().getBody().getCompositeStatement().codeRange(),
              invocation,
              RefactoringLine.VisualisationType.RIGHT,
              refactoringLine -> {
                refactoringLine.setLazyNames(new String[]{
                    null,
                    ref.getExtractedOperation().getName(),
                    null
                });
              },
              RefactoringLine.MarkingOption.EXTRACT,
              false));
      return info;
    } else {
      info.setGroup(Group.METHOD)
          .setElementBefore("from " + ref.getSourceOperationBeforeExtraction().getName())
          .setElementAfter("extracted " + ref.getExtractedOperation().getName())
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
