package data.types.variables;

import data.RefactoringInfo;
import data.TrueCodeRange;
import data.Type;
import data.types.Handler;
import gr.uom.java.xmi.diff.ChangeVariableTypeRefactoring;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class ChangeVariableTypeHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring, String commitId) {
    ChangeVariableTypeRefactoring ref = (ChangeVariableTypeRefactoring) refactoring;
    if (ref.getOriginalVariable().isParameter() && ref.getChangedTypeVariable().isParameter()) {
      return new RefactoringInfo(Type.VARIABLE)
          .setType(RefactoringType.CHANGE_PARAMETER_TYPE)
          .setName(ref.getName())
          .setText(ref.toString())
          .setCommitId(commitId)
          .setLeftSide(Arrays.asList(new TrueCodeRange(ref.getOriginalVariable().codeRange())))
          .setRightSide(Arrays.asList(new TrueCodeRange(ref.getChangedTypeVariable().codeRange())));
    }
    return new RefactoringInfo(Type.VARIABLE)
        .setType(RefactoringType.CHANGE_VARIABLE_TYPE)
        .setName(ref.getName())
        .setText(ref.toString())
        .setCommitId(commitId)
        .setLeftSide(Arrays.asList(new TrueCodeRange(ref.getOriginalVariable().codeRange())))
        .setRightSide(Arrays.asList(new TrueCodeRange(ref.getChangedTypeVariable().codeRange())));
  }
}
