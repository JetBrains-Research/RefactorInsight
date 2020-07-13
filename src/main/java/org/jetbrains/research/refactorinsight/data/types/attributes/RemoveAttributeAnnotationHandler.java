package org.jetbrains.research.refactorinsight.data.types.attributes;

import static org.jetbrains.research.refactorinsight.data.RefactoringLine.MarkingOption.REMOVE;

import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.RemoveAttributeAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.refactoringminer.api.Refactoring;

public class RemoveAttributeAnnotationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RemoveAttributeAnnotationRefactoring ref = (RemoveAttributeAnnotationRefactoring) refactoring;
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
            annotation.codeRange(),
            ref.getAttributeAfter().codeRange(),
            line -> line.addOffset(annotation.getLocationInfo(), REMOVE),
            REMOVE,
            false);
  }
}
