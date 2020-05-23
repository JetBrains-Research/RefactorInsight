package data.types.methods;

import data.RefactoringInfo;
import data.Scope;
import data.TrueCodeRange;
import data.types.Handler;
import gr.uom.java.xmi.diff.ModifyMethodAnnotationRefactoring;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class ModifyMethodAnnotationHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    ModifyMethodAnnotationRefactoring ref = (ModifyMethodAnnotationRefactoring) refactoring;
    return new RefactoringInfo(Scope.METHOD)
        .setType(RefactoringType.MODIFY_METHOD_ANNOTATION)
        .setName(ref.getName())
        .setText(ref.toString())
        .setElementBefore(ref.getAnnotationBefore().toString())
        .setElementAfter(ref.getAnnotationAfter().toString())
        .setLeftSide(Arrays.asList(new TrueCodeRange(ref.getAnnotationBefore().codeRange())))
        .setRightSide(Arrays.asList(new TrueCodeRange(ref.getAnnotationAfter().codeRange())))
        .setNameBefore(Handler.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(Handler.calculateSignature(ref.getOperationAfter()));
  }
}
