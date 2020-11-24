package org.jetbrains.research.refactorinsight.data.types;

import org.jetbrains.research.refactorinsight.adapters.RefactoringType;
import org.jetbrains.research.refactorinsight.data.RefactoringEntry;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

/**
 * This Handler creates the RefactoringInfo object.
 * Its implementations handle the refactoring types that RefactoringMiner supports.
 */
public abstract class Handler {

  /**
   * Start generating RefactoringInfo from Refactoring.
   *
   * @param refactoring Refactoring from RefactoringMiner.
   * @param entry       Refactoring entry to handle.
   * @return RefactoringInfo
   */
  public RefactoringInfo handle(Refactoring refactoring, RefactoringEntry entry) {
    RefactoringInfo info = new RefactoringInfo()
        .setType(RefactoringType.valueOf(refactoring.getRefactoringType().name()))
        .setName(refactoring.getName())
        .setEntry(entry);
    return specify(refactoring, info);
  }

  /**
   * Start generating RefactoringInfo from Refactoring.
   *
   * @param refactoring Refactoring from kotlinRMiner.
   * @param entry       Refactoring entry to handle.
   * @return RefactoringInfo.
   */
  public RefactoringInfo handle(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring,
                                RefactoringEntry entry) {
    RefactoringInfo info = new RefactoringInfo()
        .setType(RefactoringType.valueOf(refactoring.getRefactoringType().name()))
        .setName(refactoring.getName())
        .setEntry(entry);
    return specify(refactoring, info);
  }

  public abstract RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info);

  public abstract RefactoringInfo specify(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring,
                                          RefactoringInfo info);

}
