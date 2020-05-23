package data.types.packages;

import data.RefactoringInfo;
import data.TrueCodeRange;
import data.Type;
import data.types.Handler;
import gr.uom.java.xmi.diff.RenamePackageRefactoring;
import java.util.stream.Collectors;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class RenamePackageHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    RenamePackageRefactoring ref = (RenamePackageRefactoring) refactoring;
    return new RefactoringInfo(Type.PACKAGE)
        .setType(RefactoringType.RENAME_PACKAGE)
        .setName(ref.getName())
        .setText(ref.toString())
        .setLeftSide(ref.leftSide().stream().map(TrueCodeRange::new).collect(Collectors.toList()))
        .setRightSide(
            ref.rightSide().stream().map(TrueCodeRange::new).collect(Collectors.toList()));
  }
}
