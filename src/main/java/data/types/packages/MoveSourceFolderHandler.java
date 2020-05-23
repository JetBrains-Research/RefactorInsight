package data.types.packages;

import data.RefactoringInfo;
import data.Scope;
import data.TrueCodeRange;
import data.types.Handler;
import gr.uom.java.xmi.diff.MoveSourceFolderRefactoring;
import java.util.stream.Collectors;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class MoveSourceFolderHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    MoveSourceFolderRefactoring ref = (MoveSourceFolderRefactoring) refactoring;
    return new RefactoringInfo(Scope.PACKAGE)
        .setType(RefactoringType.MOVE_SOURCE_FOLDER)
        .setName(ref.getName())
        .setText(ref.toString())
        .setLeftSide(ref.leftSide().stream().map(TrueCodeRange::new).collect(Collectors.toList()))
        .setRightSide(
            ref.rightSide().stream().map(TrueCodeRange::new).collect(Collectors.toList()));
  }
}
