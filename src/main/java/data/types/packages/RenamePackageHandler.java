package data.types.packages;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.RenamePackageRefactoring;
import org.refactoringminer.api.Refactoring;

public class RenamePackageHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RenamePackageRefactoring ref = (RenamePackageRefactoring) refactoring;
    //TODO same for the rename of a package
    return info.setGroup(Group.PACKAGE).setNameBefore(ref.getPattern().getBefore())
        .setNameAfter(ref.getPattern().getAfter());
  }
}
