package org.jetbrains.research.refactorinsight.data.types.classes;

import static org.jetbrains.research.refactorinsight.data.RefactoringLine.MarkingOption.REMOVE;

import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.RemoveClassAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.adapters.LocationInfo;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.refactoringminer.api.Refactoring;

public class RemoveClassAnnotationHandler extends Handler {

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
                REMOVE),
            REMOVE,
            false);
  }

  @Override
  public RefactoringInfo specify(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring,
                                 RefactoringInfo info) {
    //This kind of refactoring is not supported by kotlinRMiner yet.
    return null;
  }
}
