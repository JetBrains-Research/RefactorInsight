package org.jetbrains.research.refactorinsight.kotlin.impl.data.packages;

import org.jetbrains.research.kotlinrminer.api.Refactoring;
import org.jetbrains.research.kotlinrminer.diff.refactoring.MoveSourceFolderRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.KotlinRefactoringHandler;

public class MoveSourceFolderKotlinHandler extends KotlinRefactoringHandler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring,
                                 RefactoringInfo info) {
    MoveSourceFolderRefactoring ref =
        (MoveSourceFolderRefactoring) refactoring;
    return info.setGroup(Group.PACKAGE)
        .setNameBefore(ref.getPattern().getBefore())
        .setNameAfter(ref.getPattern().getAfter());
  }

}
