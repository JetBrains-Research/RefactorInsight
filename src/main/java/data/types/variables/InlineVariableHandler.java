package data.types.variables;

import data.RefactoringInfo;
import data.TrueCodeRange;
import data.Type;
import data.types.Handler;
import gr.uom.java.xmi.diff.InlineVariableRefactoring;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class InlineVariableHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    InlineVariableRefactoring ref = (InlineVariableRefactoring) refactoring;
    return new RefactoringInfo(Type.VARIABLE)
        .setType(RefactoringType.INLINE_VARIABLE)
        .setName(ref.getName())
        .setText(ref.toString())
        .setNameBefore(ref.getVariableDeclaration().getVariableName())
        .setNameAfter(" in method " + ref.getOperationAfter().getName())
        .setLeftSide(Arrays.asList(new TrueCodeRange(ref.getVariableDeclaration().codeRange())))
        .setRightSide(
            ref.rightSide().stream().map(TrueCodeRange::new).collect(Collectors.toList()));
  }
}
