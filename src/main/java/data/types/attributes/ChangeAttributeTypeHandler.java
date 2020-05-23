package data.types.attributes;

import data.RefactoringInfo;
import data.Scope;
import data.TrueCodeRange;
import data.types.Handler;
import gr.uom.java.xmi.diff.ChangeAttributeTypeRefactoring;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class ChangeAttributeTypeHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    ChangeAttributeTypeRefactoring ref = (ChangeAttributeTypeRefactoring) refactoring;
    return new RefactoringInfo(Scope.ATTRIBUTE)
        .setType(RefactoringType.CHANGE_ATTRIBUTE_TYPE)
        .setName(ref.getName())
        .setText(ref.toString())
        .setNameBefore(ref.getOriginalAttribute().getType().toString())
        .setNameAfter(ref.getChangedTypeAttribute().getType().toString())
        .setLeftSide(Arrays.asList(new TrueCodeRange(ref.getOriginalAttribute().codeRange())))
        .setRightSide(Arrays.asList(new TrueCodeRange(ref.getChangedTypeAttribute().codeRange())));
  }
}
