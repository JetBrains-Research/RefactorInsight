package data.types.methods;

import static data.RefactoringLine.MarkingOption.REMOVE;

import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.RemoveMethodAnnotationRefactoring;
import org.refactoringminer.api.Refactoring;
import utils.StringUtils;

public class RemoveMethodAnnotationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RemoveMethodAnnotationRefactoring ref = (RemoveMethodAnnotationRefactoring) refactoring;
    UMLAnnotation annotation = ref.getAnnotation();

    String classNameBefore = ref.getOperationBefore().getClassName();
    String classNameAfter = ref.getOperationAfter().getClassName();

    return info.setGroup(RefactoringInfo.Group.METHOD)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setElementBefore(ref.getAnnotation().toString())
        .setElementAfter(null)
        .addMarking(
            annotation.codeRange(),
            ref.getOperationAfter().codeRange(),
            line -> line.addOffset(annotation.getLocationInfo(),
                REMOVE),
            REMOVE,
            false)
        .setNameBefore(StringUtils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(StringUtils.calculateSignature(ref.getOperationAfter()));
  }
}
