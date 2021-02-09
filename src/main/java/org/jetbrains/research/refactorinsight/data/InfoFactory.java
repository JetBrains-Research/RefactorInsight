package org.jetbrains.research.refactorinsight.data;

import org.jetbrains.research.refactorinsight.adapters.RefactoringType;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.refactoringminer.api.Refactoring;

/**
 * Creates {@link RefactoringInfo} objects for refactorings provided by RefactoringMiner and kotlinRMiner.
 */
public class InfoFactory {

  /**
   * Creates a relevant {@link RefactoringInfo} instance for a given Refactoring provided by RefactoringMiner.
   *
   * @param refactoring to be analyzed.
   * @return resulting RefactoringInfo.
   */
  public RefactoringInfo create(Refactoring refactoring, RefactoringEntry entry) {
    final Handler handler = RefactoringType.valueOf(refactoring.getRefactoringType().name()).getHandler();
    return handler.handle(refactoring, entry);
  }

  /**
   * Creates a relevant {@link RefactoringInfo} instance for a given Refactoring provided by kotlinRMiner.
   *
   * @param refactoring to be analyzed.
   * @return resulting RefactoringInfo.
   */
  public RefactoringInfo create(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring,
                                RefactoringEntry entry) {
    final Handler handler = RefactoringType.valueOf(refactoring.getRefactoringType().name()).getHandler();
    return handler.handle(refactoring, entry);
  }

}
