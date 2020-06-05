package data.types.methods;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.LocalFilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.ContentRevision;
import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import git4idea.GitContentRevision;
import git4idea.GitRevisionNumber;
import gr.uom.java.xmi.diff.RenameOperationRefactoring;
import java.util.List;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

public class RenameMethodHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    RenameOperationRefactoring ref = (RenameOperationRefactoring) refactoring;

    String id = ref.getRenamedOperation().getClassName() + ".";
    if (ref.getRenamedOperation().isGetter()) {
      id += ref.getRenamedOperation().getBody().getAllVariables().get(0);
      info.setGroupId(id);
    }
    if (ref.getRenamedOperation().isSetter()) {
      id += ref.getRenamedOperation().getParameterNameList().get(0);
      info.setGroupId(id);
    }

    return info.setGroup(Group.METHOD)
        .setElementBefore(null)
        .setElementAfter(null)
        .addMarking(ref.getOriginalOperation().getBody().getCompositeStatement().codeRange(),
            ref.getRenamedOperation().getBody().getCompositeStatement().codeRange(),
            refactoringLine -> refactoringLine.setHasColumns(false))
        .addMarking(
            ref.getOriginalOperation().getBody().getCompositeStatement().codeRange().getStartLine(),
            ref.getOriginalOperation().getBody().getCompositeStatement().codeRange().getStartLine(),
            ref.getRenamedOperation().getBody().getCompositeStatement().codeRange().getStartLine(),
            ref.getRenamedOperation().getBody().getCompositeStatement().codeRange().getStartLine(),
            ref.getSourceOperationCodeRangeBeforeRename().getFilePath(),
            ref.getTargetOperationCodeRangeAfterRename().getFilePath(),
            refactoringLine -> {
              List<String> parents = info.getParents();
              try {
                FilePath beforePath = new LocalFilePath(
                    project.getBasePath() + "/"
                        + ref.getSourceOperationCodeRangeBeforeRename().getFilePath(), false);
                FilePath afterPath = new LocalFilePath(
                    project.getBasePath() + "/"
                        + ref.getTargetOperationCodeRangeAfterRename().getFilePath(), false);
                GitRevisionNumber afterNumber = new GitRevisionNumber(info.getCommitId());
                GitRevisionNumber beforeNumber = new GitRevisionNumber(parents.get(0));

                ContentRevision
                    before = GitContentRevision.createRevision(beforePath, beforeNumber, project);

                ContentRevision
                    after = GitContentRevision.createRevision(afterPath, afterNumber, project);

                int[] beforeColumns =
                    Utils.findColumns(before.getContent(), ref.getOriginalOperation().getName(),
                        ref.getOriginalOperation().getBody().getCompositeStatement().codeRange()
                            .getStartLine());

                int[] afterColumns =
                    Utils.findColumns(after.getContent(), ref.getRenamedOperation().getName(),
                        ref.getRenamedOperation().getBody().getCompositeStatement().codeRange()
                            .getStartLine());

                refactoringLine.setColumns(
                    new int[] {beforeColumns[0], beforeColumns[1], 0, 0, afterColumns[0],
                        afterColumns[1]});

              } catch (VcsException e) {
                e.printStackTrace();
              }
            })
        .setNameBefore(Utils.calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(Utils.calculateSignature(ref.getRenamedOperation()));
  }
}
