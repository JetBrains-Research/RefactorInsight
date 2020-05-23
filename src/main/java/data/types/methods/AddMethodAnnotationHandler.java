package data.types.methods;

import data.RefactoringInfo;
import data.Scope;
import data.TrueCodeRange;
import data.types.Handler;
import gr.uom.java.xmi.diff.AddMethodAnnotationRefactoring;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class AddMethodAnnotationHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    AddMethodAnnotationRefactoring ref = (AddMethodAnnotationRefactoring) refactoring;
    TrueCodeRange left = new TrueCodeRange(ref.getOperationBefore().codeRange());
    TrueCodeRange right = new TrueCodeRange(ref.getAnnotation().codeRange());

    return new RefactoringInfo(Scope.METHOD)
        .setType(RefactoringType.ADD_METHOD_ANNOTATION)
        .setName(ref.getName())
        .setText(ref.toString())
        .setElementBefore(null)
        .setElementAfter(ref.getAnnotation().toString())
        .setLeftSide(Arrays.asList(left))
        .setRightSide(Arrays.asList(right))
        .setNameBefore(Handler.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(Handler.calculateSignature(ref.getOperationBefore()));
  }
}
