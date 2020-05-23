package data.types.attributes;

import data.RefactoringEntry;
import data.RefactoringInfo;
import data.TrueCodeRange;
import data.Type;
import data.types.Handler;
import gr.uom.java.xmi.diff.PullUpAttributeRefactoring;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class PullUpAttributeHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    PullUpAttributeRefactoring ref = (PullUpAttributeRefactoring) refactoring;
    return new RefactoringInfo(Type.ATTRIBUTE)
        .setType(RefactoringType.PULL_UP_ATTRIBUTE)
        .setName(ref.getName())
        .setText(ref.toString())
        .setNameBefore(ref.getSourceClassName())
        .setNameAfter(ref.getTargetClassName())
        .setLeftSide(Arrays.asList(new TrueCodeRange(ref.getOriginalAttribute().codeRange())))
        .setRightSide(Arrays.asList(new TrueCodeRange(ref.getMovedAttribute().codeRange())));
  }
}
