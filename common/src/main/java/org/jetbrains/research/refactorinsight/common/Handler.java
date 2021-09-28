package org.jetbrains.research.refactorinsight.common;

import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

/**
 * Handles provided refactorings and creates {@link RefactoringInfo} instances.
 */
public abstract class Handler {

  /**
   * Creates an {@link RefactoringInfo} instance from {@link Refactoring}.
   *
   * @param refactoring refactoring from RefactoringMiner.
   * @return RefactoringInfo.
   */
  public RefactoringInfo handle(Refactoring refactoring) {
    RefactoringInfo info = new RefactoringInfo();
    return specify(refactoring, info);
  }

  /**
   * Creates an {@link RefactoringInfo} instance from {@link org.jetbrains.research.kotlinrminer.api.Refactoring}.
   *
   * @param refactoring refactoring from kotlinRMiner.
   * @return RefactoringInfo.
   */
  public RefactoringInfo handle(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring) {
    RefactoringInfo info = new RefactoringInfo();
    return specify(refactoring, info);
  }

  // Should be overrided in the specific handler implementation for Java refactorings
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    return null;
  }

  // Should be overrided in the specific handler implementation for Kotlin refactorings
  public RefactoringInfo specify(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring,
                                          RefactoringInfo info) {
      return null;
  }

}
