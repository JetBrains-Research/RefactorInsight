package org.jetbrains.research.refactorinsight.data.types.packages;

import gr.uom.java.xmi.diff.MoveSourceFolderRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

public class MoveSourceFolderJavaHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    MoveSourceFolderRefactoring ref = (MoveSourceFolderRefactoring) refactoring;
    return info.setGroup(Group.PACKAGE)
        .setNameBefore(ref.getPattern().getBefore())
        .setNameAfter(ref.getPattern().getAfter());
  }

}
