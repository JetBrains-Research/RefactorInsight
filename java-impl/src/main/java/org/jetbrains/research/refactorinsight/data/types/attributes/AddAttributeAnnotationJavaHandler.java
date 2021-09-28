package org.jetbrains.research.refactorinsight.data.types.attributes;

import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.AddAttributeAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.adapters.LocationInfo;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.refactoringminer.api.Refactoring;

public class AddAttributeAnnotationJavaHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    AddAttributeAnnotationRefactoring ref = (AddAttributeAnnotationRefactoring) refactoring;
    UMLAnnotation annotation = ref.getAnnotation();

    String classNameBefore = ref.getAttributeBefore().getClassName();
    String classNameAfter = ref.getAttributeAfter().getClassName();

    return info.setGroup(Group.ATTRIBUTE)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setNameBefore(ref.getAttributeBefore().getVariableDeclaration().toQualifiedString())
        .setNameAfter(ref.getAttributeAfter().getVariableDeclaration().toQualifiedString())
        .setElementBefore(ref.getAnnotation().toString())
        .setElementAfter(null)
        .addMarking(
            new CodeRange(ref.getAttributeBefore().codeRange()),
            new CodeRange(annotation.codeRange()),
            line -> line.addOffset(new LocationInfo(annotation.getLocationInfo()), RefactoringLine.MarkingOption.ADD),
            RefactoringLine.MarkingOption.ADD,
            false);
  }

}
