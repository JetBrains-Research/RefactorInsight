package data.types.packages;

import data.RefactoringInfo;
import data.TrueCodeRange;
import data.Type;
import data.types.Handler;
import gr.uom.java.xmi.diff.MoveSourceFolderRefactoring;
import java.util.stream.Collectors;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class MoveSourceFolderHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring, String commitId) {
    MoveSourceFolderRefactoring ref = (MoveSourceFolderRefactoring) refactoring;
    return new RefactoringInfo(Type.PACKAGE)
        .setType(RefactoringType.MOVE_SOURCE_FOLDER)
        .setName(ref.getName())
        .setText(ref.toString())
        .setCommitId(commitId)
        .setLeftSide(ref.leftSide().stream().map(TrueCodeRange::new).collect(Collectors.toList()))
        .setRightSide(
            ref.rightSide().stream().map(TrueCodeRange::new).collect(Collectors.toList()));
  }
}
