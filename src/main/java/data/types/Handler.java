package data.types;

import data.RefactoringEntry;
import data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

/**
 * This Handler creates the RefactoringInfo object.
 * Its implementations handle the refactoring types that RefactoringMiner supports.
 */
public abstract class Handler {

  /**
   * Start generating RefactoringInfo from Refactoring.
   *
   * @param refactoring Refactoring from RefactoringMiner
   * @param entry Refactoring entry to handle
   * @return RefactoringInfo
   */
  public RefactoringInfo handle(Refactoring refactoring, RefactoringEntry entry) {
    RefactoringInfo info = new RefactoringInfo()
        .setType(refactoring.getRefactoringType())
        .setName(refactoring.getName())
        .setEntry(entry);
    return specify(refactoring, info);
  }

  public abstract RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info);


}
