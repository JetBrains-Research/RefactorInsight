package org.jetbrains.research.refactorinsight.data.types.methods;

import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.refactoringminer.api.Refactoring;

public class MergeOperationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    //This kind of refactoring is not supported by RefactoringMiner yet.
    return null;
  }

  @Override
  public RefactoringInfo specify(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring,
                                 RefactoringInfo info) {
    //This kind of refactoring is not supported by kotlinRMiner yet.
    return null;
  }
}
