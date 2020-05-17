package data.types.methods;

import data.RefactoringInfo;
import data.TrueCodeRange;
import data.Type;
import data.types.Handler;
import gr.uom.java.xmi.diff.AddMethodAnnotationRefactoring;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class AddMethodAnnotationHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring, String commitId) {
    AddMethodAnnotationRefactoring ref = (AddMethodAnnotationRefactoring) refactoring;
    TrueCodeRange left = new TrueCodeRange(ref.getOperationBefore().codeRange());
    TrueCodeRange right = new TrueCodeRange(ref.getAnnotation().codeRange());

    return new RefactoringInfo(Type.METHOD)
        .setType(RefactoringType.ADD_METHOD_ANNOTATION)
        .setName(ref.getName())
        .setText(ref.toString())
        .setCommitId(commitId)
        .setLeftSide(Arrays.asList(left))
        .setRightSide(Arrays.asList(right))
        .setNameBefore(Handler.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(Handler.calculateSignature(ref.getOperationBefore()));
  }
}
