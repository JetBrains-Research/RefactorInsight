package org.jetbrains.research.refactorinsight.data.types.classes;

import org.jetbrains.research.refactorinsight.common.diff.VisualizationType;
import org.jetbrains.research.kotlinrminer.api.Refactoring;
import org.jetbrains.research.kotlinrminer.diff.refactoring.ExtractClassRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;

public class ExtractClassKotlinHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring,
                                 RefactoringInfo info) {
    ExtractClassRefactoring ref = (ExtractClassRefactoring) refactoring;

    if (ref.getExtractedClass().isInterface()) {
      info.setGroup(Group.INTERFACE);
    } else if (ref.getExtractedClass().isAbstract()) {
      info.setGroup(Group.ABSTRACT);
    } else {
      info.setGroup(Group.CLASS);
    }
    info.setDetailsBefore(ref.getOriginalClass().getPackageName())
        .setDetailsAfter(ref.getExtractedClass().getPackageName())
        .setElementBefore(ref.getOriginalClass().getName())
        .setElementAfter(ref.getExtractedClass().getName())
        .setNameBefore(ref.getExtractedClass().getName())
        .setNameAfter(ref.getExtractedClass().getName());

    if (ref.getAttributeOfExtractedClassTypeInOriginalClass() != null) {
      info.setThreeSided(true);
      ref.getExtractedOperations().forEach(operation -> info.addMarking(
          new CodeRange(operation.codeRange()),
          new CodeRange(ref.getExtractedClass().codeRange()),
          new CodeRange(ref.getAttributeOfExtractedClassTypeInOriginalClass().codeRange()),
              VisualizationType.LEFT,
          null,
          RefactoringLine.MarkingOption.NONE,
          true));

      ref.getExtractedAttributes().forEach(operation -> info.addMarking(
          new CodeRange(operation.codeRange()),
          new CodeRange(ref.getExtractedClass().codeRange()),
          new CodeRange(ref.getAttributeOfExtractedClassTypeInOriginalClass().codeRange()),
              VisualizationType.LEFT,
          null,
          RefactoringLine.MarkingOption.NONE,
          true));


      String[] nameSpace = ref.getExtractedClass().getName().split("\\.");
      String className = nameSpace[nameSpace.length - 1];

      info.addMarking(
          new CodeRange(ref.getOriginalClass().codeRange()),
          new CodeRange(ref.getExtractedClass().codeRange()),
          new CodeRange(ref.getAttributeOfExtractedClassTypeInOriginalClass().codeRange()),
              VisualizationType.RIGHT,
          refactoringLine -> refactoringLine.setWord(new String[]{
              null,
              className,
              null
          }),
          RefactoringLine.MarkingOption.EXTRACT,
          true);
    } else {
      ref.getExtractedOperations().forEach(operation -> info.addMarking(
          new CodeRange(operation.codeRange()),
          new CodeRange(ref.getExtractedClass().codeRange()), true));

      ref.getExtractedAttributes().forEach(operation -> info.addMarking(
          new CodeRange(operation.codeRange()),
          new CodeRange(ref.getExtractedClass().codeRange()), true));
    }
    return info;
  }
}
