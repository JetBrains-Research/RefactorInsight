package data.types.variables;

import data.RefactoringInfo;
import data.Scope;
import data.TrueCodeRange;
import data.types.Handler;
import gr.uom.java.xmi.diff.ChangeReturnTypeRefactoring;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class ChangeReturnTypeHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    ChangeReturnTypeRefactoring ref = (ChangeReturnTypeRefactoring) refactoring;
    return new RefactoringInfo(Scope.VARIABLE)
        .setType(RefactoringType.CHANGE_RETURN_TYPE)
        .setName(ref.getName())
        .setText(ref.toString())
        .setNameBefore(ref.getOriginalType().toString())
        .setNameAfter(ref.getChangedType().toString())
        .setLeftSide(Arrays.asList(new TrueCodeRange(ref.getOperationBefore().codeRange())))
        .setRightSide(Arrays.asList(new TrueCodeRange(ref.getOperationAfter().codeRange())));
  }

}
