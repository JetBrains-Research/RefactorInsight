package org.jetbrains.research.refactorinsight.data.types.methods;

import static org.jetbrains.research.refactorinsight.data.RefactoringLine.MarkingOption.REMOVE;

import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.RemoveMethodAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.adapters.LocationInfo;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.jetbrains.research.refactorinsight.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

public class RemoveMethodAnnotationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RemoveMethodAnnotationRefactoring ref = (RemoveMethodAnnotationRefactoring) refactoring;
    UMLAnnotation annotation = ref.getAnnotation();

    String classNameBefore = ref.getOperationBefore().getClassName();
    String classNameAfter = ref.getOperationAfter().getClassName();

    return info.setGroup(Group.METHOD)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setElementBefore(ref.getAnnotation().toString())
        .setElementAfter(null)
        .addMarking(
            new CodeRange(annotation.codeRange()),
            new CodeRange(ref.getOperationAfter().codeRange()),
            line -> line.addOffset(new LocationInfo(annotation.getLocationInfo()),
                REMOVE),
            REMOVE,
            false)
        .setNameBefore(StringUtils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(StringUtils.calculateSignature(ref.getOperationAfter()));
  }

  @Override
  public RefactoringInfo specify(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring,
                                 RefactoringInfo info) {
    //This kind of refactoring is not supported by kotlinRMiner yet.
    return null;
  }
}
