package data.types.methods;

import data.RefactoringInfo;
import data.Scope;
import data.TrueCodeRange;
import data.types.Handler;
import gr.uom.java.xmi.diff.RemoveMethodAnnotationRefactoring;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class RemoveMethodAnnotationHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    RemoveMethodAnnotationRefactoring ref = (RemoveMethodAnnotationRefactoring) refactoring;
    return new RefactoringInfo(Scope.METHOD)
        .setType(RefactoringType.REMOVE_METHOD_ANNOTATION)
        .setText(ref.toString())
        .setName(ref.getName())
        .setElementBefore(ref.getAnnotation().toString())
        .setElementAfter(null)
        .setLeftSide(
            Arrays.asList(new TrueCodeRange(ref.getAnnotation().codeRange())))
        .setRightSide(
            Arrays.asList(new TrueCodeRange(ref.getOperationAfter().codeRange())))
        .setNameBefore(Handler.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(Handler.calculateSignature(ref.getOperationAfter()));
  }
}
