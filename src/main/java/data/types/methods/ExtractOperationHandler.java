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
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
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

      int[] midColumns = new int[] {1, 1};
      try {
        String absolutePath =
            project.getBasePath() + "/" + ref.getExtractedOperationCodeRange().getFilePath();
        String midText = GitContentRevision.createRevision(
            new LocalFilePath(absolutePath, false),
            new GitRevisionNumber(info.getCommitId()), project).getContent();
        midColumns = Utils.findColumns(midText, ref.getExtractedOperation().getName(),
            ref.getExtractedOperation().getBody().getCompositeStatement().codeRange()
                .getStartLine());
      } catch (VcsException e) {
        e.printStackTrace();
      }

      int[] finalMidColumns = midColumns;
      ref.getExtractedOperationInvocationCodeRanges().forEach(invocation ->
          info.addMarking(
              ref.getSourceOperationCodeRangeBeforeExtraction(), //TODO make (1,1)
              ref.getExtractedOperation().getBody().getCompositeStatement().codeRange(),
              invocation,
              RefactoringLine.VisualisationType.RIGHT,
              refactoringLine -> {
                refactoringLine.setColumns(new int[] {1, 1, finalMidColumns[0], finalMidColumns[1],
                    invocation.getStartColumn(), invocation.getEndColumn()});
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
