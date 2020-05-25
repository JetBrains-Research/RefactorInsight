package data.types.packages;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.MoveSourceFolderRefactoring;
import org.refactoringminer.api.Refactoring;

public class MoveSourceFolderHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    MoveSourceFolderRefactoring ref = (MoveSourceFolderRefactoring) refactoring;
    //TODO i dont really think a diff window is the best way to display a change of folder
    return info.setGroup(Group.PACKAGE)
        .setNameBefore(ref.getPattern().getBefore())
        .setNameAfter(ref.getPattern().getAfter());
  }
}
