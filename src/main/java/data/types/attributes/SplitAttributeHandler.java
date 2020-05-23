package data.types.attributes;

import data.RefactoringInfo;
import data.TrueCodeRange;
import data.Type;
import data.types.Handler;
import gr.uom.java.xmi.diff.SplitAttributeRefactoring;
import java.util.stream.Collectors;
import org.refactoringminer.api.Refactoring;

public class SplitAttributeHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    SplitAttributeRefactoring ref = (SplitAttributeRefactoring) refactoring;
    return new RefactoringInfo(Type.ATTRIBUTE)
        .setType(ref.getRefactoringType())
        .setName(ref.getName())
        .setText(ref.toString())
        .setNameBefore(ref.getOldAttribute().getVariableName())
        .setNameAfter(ref.getSplitAttributes().stream().map(x -> x.getVariableName()).collect(
            Collectors.joining()))
        .setLeftSide(ref.leftSide().stream().map(TrueCodeRange::new).collect(Collectors.toList()))
        .setRightSide(
            ref.rightSide().stream().map(TrueCodeRange::new).collect(Collectors.toList()));
  }
}
