package org.jetbrains.research.refactorinsight.processors;

import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
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
  public RefactoringInfo create(Refactoring refactoring) {
    final Handler handler = RefactoringType.valueOf(refactoring.getRefactoringType().name()).getJavaHandler();
    RefactoringInfo refactoringInfo = handler.handle(refactoring);
    refactoringInfo.setType(RefactoringType.valueOf(refactoring.getRefactoringType().name()));
    return refactoringInfo;
  }

  /**
   * Creates a relevant {@link RefactoringInfo} instance for a given Refactoring provided by kotlinRMiner.
   *
   * @param refactoring to be analyzed.
   * @return resulting RefactoringInfo.
   */
  public RefactoringInfo create(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring) {
    final Handler handler = RefactoringType.valueOf(refactoring.getRefactoringType().name()).getKotlinHandler();
    RefactoringInfo refactoringInfo = handler.handle(refactoring);
    refactoringInfo.setType(RefactoringType.valueOf(refactoring.getRefactoringType().name()));
    return refactoringInfo;
  }

}