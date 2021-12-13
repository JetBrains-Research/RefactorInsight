package org.jetbrains.research.refactorinsight.java.impl.data.packages;

import gr.uom.java.xmi.diff.MoveSourceFolderRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.java.impl.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

public class MoveSourceFolderJavaHandler extends JavaRefactoringHandler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    MoveSourceFolderRefactoring ref = (MoveSourceFolderRefactoring) refactoring;
    return info.setGroup(Group.PACKAGE)
        .setNameBefore(ref.getPattern().getBefore())
        .setNameAfter(ref.getPattern().getAfter());
  }

}
