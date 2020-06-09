package data.types;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.LocalFilePath;
import com.intellij.openapi.vcs.VcsException;
import data.RefactoringEntry;
import data.RefactoringInfo;
import git4idea.GitContentRevision;
import git4idea.GitRevisionNumber;
import org.refactoringminer.api.Refactoring;

public abstract class Handler {
  public static double time = 0;

  /**
   * Start generating RefactoringInfo from Refactoring.
   *
   * @param refactoring Refactoring from RefactoringMiner
   * @return RefactoringInfo
   */
  public RefactoringInfo handle(Refactoring refactoring, RefactoringEntry entry, Project project) {
    RefactoringInfo info = new RefactoringInfo()
        .setType(refactoring.getRefactoringType())
        .setName(refactoring.getName())
        .setEntry(entry);
    System.out.println(refactoring.getName());
    return check(specify(refactoring, info), project);
  }

  public abstract RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info);

  public RefactoringInfo check(RefactoringInfo info, Project project) {

    FilePath beforePath = new LocalFilePath(
        project.getBasePath() + "/"
            + info.getLeftPath(), false);
    FilePath midPath = !info.isThreeSided() ? null : new LocalFilePath(
        project.getBasePath() + "/"
            + info.getMidPath(), false);
    FilePath afterPath = new LocalFilePath(
        project.getBasePath() + "/"
            + info.getRightPath(), false);
    GitRevisionNumber afterNumber = new GitRevisionNumber(info.getCommitId());
    GitRevisionNumber beforeNumber = new GitRevisionNumber(info.getParent());

    try {
      String before = GitContentRevision
          .createRevision(beforePath, beforeNumber, project).getContent();
      String mid = !info.isThreeSided() ? null : GitContentRevision
          .createRevision(midPath, afterNumber, project).getContent();
      String after = GitContentRevision
          .createRevision(afterPath, afterNumber, project).getContent();

      info.getLineMarkings().forEach(l -> l.correctLines(before, mid, after));

    } catch (VcsException e) {
      e.printStackTrace();
    }

    return info;
  }
}
