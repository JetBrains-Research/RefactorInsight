package data.types.attributes;

import data.RefactoringInfo;
import data.Scope;
import data.TrueCodeRange;
import data.types.Handler;
import gr.uom.java.xmi.diff.ReplaceAttributeRefactoring;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class ReplaceAttributeHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    ReplaceAttributeRefactoring ref = (ReplaceAttributeRefactoring) refactoring;
    return new RefactoringInfo(Scope.ATTRIBUTE)
        .setType(RefactoringType.REPLACE_ATTRIBUTE)
        .setName(ref.getName())
        .setText(ref.toString())
        .setNameBefore(ref.getOriginalAttribute().getName())
        .setNameAfter(ref.getMovedAttribute().getName())
        .setLeftSide(Arrays.asList(new TrueCodeRange(ref.getOriginalAttribute().codeRange())))
        .setRightSide(
            ref.rightSide().stream().map(TrueCodeRange::new).collect(Collectors.toList()));
  }
}
