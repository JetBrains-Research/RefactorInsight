package data.types.variables;

import data.RefactoringInfo;
import data.TrueCodeRange;
import data.Type;
import data.types.Handler;
import gr.uom.java.xmi.diff.MergeVariableRefactoring;
import java.util.stream.Collectors;
import org.refactoringminer.api.Refactoring;

public class MergeVariableHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    MergeVariableRefactoring ref = (MergeVariableRefactoring) refactoring;
    return new RefactoringInfo(Type.VARIABLE)
        .setType(ref.getRefactoringType())
        .setName(ref.getName())
        .setText(ref.toString())
        .setNameBefore(ref.getMergedVariables().stream().map(x -> x.getVariableName()).collect(
            Collectors.joining()))
        .setNameAfter(ref.getNewVariable().getVariableName())
        .setLeftSide(ref.leftSide().stream().map(TrueCodeRange::new).collect(Collectors.toList()))
        .setRightSide(
            ref.rightSide().stream().map(TrueCodeRange::new).collect(Collectors.toList()));
  }
}
