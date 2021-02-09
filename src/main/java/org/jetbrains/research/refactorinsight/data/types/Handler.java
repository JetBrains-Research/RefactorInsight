package org.jetbrains.research.refactorinsight.data.types;

import org.jetbrains.research.refactorinsight.adapters.RefactoringType;
import org.jetbrains.research.refactorinsight.data.RefactoringEntry;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

/**
 * Handles provided refactorings and creates {@link RefactoringInfo} instances.
 */
public abstract class Handler {

  /**
   * Creates an {@link RefactoringInfo} instance from {@link Refactoring}.
   *
   * @param refactoring refactoring from RefactoringMiner.
   * @param entry       refactoring entry to handle.
   * @return RefactoringInfo.
   */
  public RefactoringInfo handle(Refactoring refactoring, RefactoringEntry entry) {
    RefactoringInfo info = new RefactoringInfo()
        .setType(RefactoringType.valueOf(refactoring.getRefactoringType().name()))
        .setName(refactoring.getName())
        .setEntry(entry);
    return specify(refactoring, info);
  }

  /**
   * Creates an {@link RefactoringInfo} instance from {@link org.jetbrains.research.kotlinrminer.api.Refactoring}.
   *
   * @param refactoring refactoring from kotlinRMiner.
   * @param entry       refactoring entry to handle.
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
