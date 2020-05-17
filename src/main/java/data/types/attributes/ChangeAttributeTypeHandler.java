package data.types.attributes;

import data.RefactoringInfo;
import data.TrueCodeRange;
import data.Type;
import data.types.Handler;
import gr.uom.java.xmi.diff.ChangeAttributeTypeRefactoring;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class ChangeAttributeTypeHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring, String commitId) {
    ChangeAttributeTypeRefactoring ref = (ChangeAttributeTypeRefactoring) refactoring;
    return new RefactoringInfo(Type.ATTRIBUTE)
        .setType(RefactoringType.CHANGE_ATTRIBUTE_TYPE)
        .setName(ref.getName())
        .setText(ref.toString())
        .setCommitId(commitId)
        .setLeftSide(Arrays.asList(new TrueCodeRange(ref.getOriginalAttribute().codeRange())))
        .setRightSide(Arrays.asList(new TrueCodeRange(ref.getChangedTypeAttribute().codeRange())));
  }
}
