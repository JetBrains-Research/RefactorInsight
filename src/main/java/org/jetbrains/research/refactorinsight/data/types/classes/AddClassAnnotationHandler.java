package org.jetbrains.research.refactorinsight.data.types.classes;

import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.AddClassAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.adapters.LocationInfo;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.refactoringminer.api.Refactoring;

public class AddClassAnnotationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    AddClassAnnotationRefactoring ref = (AddClassAnnotationRefactoring) refactoring;
    UMLAnnotation annotation = ref.getAnnotation();
    if (ref.getClassAfter().isAbstract()) {
      info.setGroup(Group.ABSTRACT);
    } else if (ref.getClassAfter().isInterface()) {
      info.setGroup(Group.INTERFACE);
    } else {
      info.setGroup(Group.CLASS);
    }
    return info
        .setDetailsBefore(ref.getClassBefore().getPackageName())
        .setDetailsAfter(ref.getClassAfter().getPackageName())
        .setNameBefore(ref.getClassBefore().getName())
        .setNameAfter(ref.getClassAfter().getName())
        .setElementBefore(ref.getAnnotation().toString())
        .setElementAfter(null)
        .addMarking(
            new CodeRange(ref.getClassBefore().codeRange()),
            new CodeRange(annotation.codeRange()),
            line -> line.addOffset(
                new LocationInfo(annotation.getLocationInfo()), RefactoringLine.MarkingOption.ADD),
            RefactoringLine.MarkingOption.ADD,
            false);
  }

  @Override
  public RefactoringInfo specify(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring,
                                 RefactoringInfo info) {
    //This kind of refactoring is not supported by kotlinRMiner yet.
    return null;
  }
}
