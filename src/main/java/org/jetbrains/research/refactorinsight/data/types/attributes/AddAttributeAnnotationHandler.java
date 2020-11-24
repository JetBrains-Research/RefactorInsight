package org.jetbrains.research.refactorinsight.data.types.attributes;

import static org.jetbrains.research.refactorinsight.data.RefactoringLine.MarkingOption.ADD;

import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.AddAttributeAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.adapters.LocationInfo;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.refactoringminer.api.Refactoring;

public class AddAttributeAnnotationHandler extends Handler {

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
            line -> line.addOffset(new LocationInfo(annotation.getLocationInfo()), ADD),
            ADD,
            false);
  }

  @Override
  public RefactoringInfo specify(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring,
                                 RefactoringInfo info) {
    //This kind of refactoring is not supported by kotlinRMiner yet.
    return null;
  }
}
