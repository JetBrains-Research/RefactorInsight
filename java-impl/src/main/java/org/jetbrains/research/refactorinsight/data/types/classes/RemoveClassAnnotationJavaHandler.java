package org.jetbrains.research.refactorinsight.data.types.classes;

import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.RemoveClassAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.adapters.LocationInfo;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.refactoringminer.api.Refactoring;

public class RemoveClassAnnotationJavaHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RemoveClassAnnotationRefactoring ref = (RemoveClassAnnotationRefactoring) refactoring;
    UMLAnnotation annotation = ref.getAnnotation();

    if (ref.getClassAfter().isAbstract()) {
      info.setGroup(Group.ABSTRACT);
    } else if (ref.getClassAfter().isInterface()) {
      info.setGroup(Group.INTERFACE);
    } else {
      info.setGroup(Group.CLASS);
    }

    return info
        .setNameBefore(ref.getClassBefore().getName())
        .setNameAfter(ref.getClassAfter().getName())
        .setElementBefore(ref.getAnnotation().toString())
        .setElementAfter(null)
        .setDetailsBefore(ref.getClassBefore().getPackageName())
        .setDetailsAfter(ref.getClassAfter().getPackageName())
        .addMarking(
            new CodeRange(annotation.codeRange()),
            new CodeRange(ref.getClassAfter().codeRange()),
            line -> line.addOffset(new LocationInfo(annotation.getLocationInfo()),
                RefactoringLine.MarkingOption.REMOVE),
            RefactoringLine.MarkingOption.REMOVE,
            false);
  }

}
