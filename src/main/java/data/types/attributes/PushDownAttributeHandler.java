package data.types.attributes;

import data.RefactoringInfo;
import data.Scope;
import data.TrueCodeRange;
import data.types.Handler;
import gr.uom.java.xmi.diff.PushDownAttributeRefactoring;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class PushDownAttributeHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    PushDownAttributeRefactoring ref = (PushDownAttributeRefactoring) refactoring;
    return new RefactoringInfo(Scope.ATTRIBUTE)
        .setType(RefactoringType.PUSH_DOWN_ATTRIBUTE)
        .setName(ref.getName())
        .setText(ref.toString())
        .setNameBefore(ref.getSourceClassName())
        .setNameAfter(ref.getTargetClassName())
        .setLeftSide(Arrays.asList(new TrueCodeRange(ref.getOriginalAttribute().codeRange())))
        .setRightSide(Arrays.asList(new TrueCodeRange(ref.getMovedAttribute().codeRange())));
  }
}
