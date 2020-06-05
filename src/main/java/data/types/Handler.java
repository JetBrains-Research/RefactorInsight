package data.types;

import com.intellij.openapi.project.Project;
import data.RefactoringEntry;
import data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

public abstract class Handler {

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

    return specify(refactoring, info, project);
  }

  public abstract RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info,
                                          Project project);


}
