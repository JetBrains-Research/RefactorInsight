package data.types.methods;

import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ModifyMethodAnnotationRefactoring;
import org.refactoringminer.api.Refactoring;
import utils.StringUtils;

public class ModifyMethodAnnotationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ModifyMethodAnnotationRefactoring ref = (ModifyMethodAnnotationRefactoring) refactoring;

    String classNameBefore = ref.getOperationBefore().getClassName();
    String classNameAfter = ref.getOperationAfter().getClassName();

    return info.setGroup(RefactoringInfo.Group.METHOD)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setElementBefore(ref.getAnnotationBefore().toString())
        .setElementAfter(ref.getAnnotationAfter().toString())
        .addMarking(ref.getAnnotationBefore().codeRange(), ref.getAnnotationAfter().codeRange(),
            true)
        .setNameBefore(StringUtils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(StringUtils.calculateSignature(ref.getOperationAfter()));
  }
}
