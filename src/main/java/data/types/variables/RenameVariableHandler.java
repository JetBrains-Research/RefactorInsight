package data.types.variables;

import data.RefactoringInfo;
import data.Scope;
import data.TrueCodeRange;
import data.types.Handler;
import gr.uom.java.xmi.diff.RenameVariableRefactoring;
import java.util.stream.Collectors;
import org.refactoringminer.api.Refactoring;

public class RenameVariableHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    RenameVariableRefactoring ref = (RenameVariableRefactoring) refactoring;
    return new RefactoringInfo(Scope.VARIABLE)
        .setType(ref.getRefactoringType())
        .setName(ref.getName())
        .setText(ref.toString())
        .setNameBefore(ref.getOriginalVariable().getVariableName())
        .setNameAfter(ref.getRenamedVariable().getVariableName())
        .setLeftSide(ref.leftSide().stream().map(TrueCodeRange::new).collect(Collectors.toList()))
        .setRightSide(
            ref.rightSide().stream().map(TrueCodeRange::new).collect(Collectors.toList()));
  }
}
