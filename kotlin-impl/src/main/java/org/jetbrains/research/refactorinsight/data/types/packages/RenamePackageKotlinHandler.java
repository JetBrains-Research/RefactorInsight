package org.jetbrains.research.refactorinsight.data.types.packages;

import org.jetbrains.research.kotlinrminer.api.Refactoring;
import org.jetbrains.research.kotlinrminer.diff.refactoring.RenamePackageRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;

public class RenamePackageKotlinHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring,
                                 RefactoringInfo info) {
    RenamePackageRefactoring ref =
        (RenamePackageRefactoring) refactoring;
    return info.setGroup(Group.PACKAGE)
        .setNameBefore(ref.getPattern().getBefore())
        .setNameAfter(ref.getPattern().getAfter());
  }

}
