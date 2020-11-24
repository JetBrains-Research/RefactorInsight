package org.jetbrains.research.refactorinsight.data.types.methods;

import gr.uom.java.xmi.diff.AddVariableAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.adapters.LocationInfo;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

public class AddParameterAnnotationHandler
    extends org.jetbrains.research.refactorinsight.data.types.Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    AddVariableAnnotationRefactoring ref = (AddVariableAnnotationRefactoring) refactoring;

    String classNameBefore = ref.getOperationBefore().getClassName();
    String classNameAfter = ref.getOperationAfter().getClassName();

    return info.setGroup(Group.METHOD)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setNameBefore(StringUtils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(StringUtils.calculateSignature(ref.getOperationAfter()))
        .setElementAfter(null)
        .setElementBefore(ref.getAnnotation().toString() + " added to "
            + ref.getVariableAfter().getVariableDeclaration().getVariableName())
        .addMarking(new CodeRange(ref.getOperationBefore().codeRange()),
            new CodeRange(ref.getOperationAfter().codeRange()),
            line -> line.addOffset(
                new LocationInfo(ref.getAnnotation().getLocationInfo()), RefactoringLine.MarkingOption.ADD)
                .setHasColumns(false),
            RefactoringLine.MarkingOption.NONE,
            true);
  }

  @Override
  public RefactoringInfo specify(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring,
                                 RefactoringInfo info) {
    //This kind of refactoring is not supported by kotlinRMiner yet.
    return null;
  }
}
