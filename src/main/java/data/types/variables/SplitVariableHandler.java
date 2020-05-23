package data.types.variables;

import data.RefactoringEntry;
import data.RefactoringInfo;
import data.TrueCodeRange;
import data.Type;
import data.types.Handler;
import gr.uom.java.xmi.diff.SplitVariableRefactoring;
import java.util.stream.Collectors;
import org.refactoringminer.api.Refactoring;

public class SplitVariableHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    SplitVariableRefactoring ref = (SplitVariableRefactoring) refactoring;
    return new RefactoringInfo(Type.VARIABLE)
        .setType(ref.getRefactoringType())
        .setName(ref.getName())
        .setText(ref.toString())
        .setNameBefore(ref.getOldVariable().getVariableName())
        .setNameAfter(ref.getSplitVariables().stream().map(x -> x.getVariableName()).collect(
            Collectors.joining()))
        .setLeftSide(ref.leftSide().stream().map(TrueCodeRange::new).collect(Collectors.toList()))
        .setRightSide(
            ref.rightSide().stream().map(TrueCodeRange::new).collect(Collectors.toList()));
  }
}
