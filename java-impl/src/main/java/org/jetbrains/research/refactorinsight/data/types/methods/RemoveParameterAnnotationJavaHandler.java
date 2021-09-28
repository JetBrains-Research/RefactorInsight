package org.jetbrains.research.refactorinsight.data.types.methods;

import gr.uom.java.xmi.diff.RemoveVariableAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.adapters.LocationInfo;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

public class RemoveParameterAnnotationJavaHandler extends Handler {
  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RemoveVariableAnnotationRefactoring ref = (RemoveVariableAnnotationRefactoring) refactoring;
    String classNameBefore = ref.getOperationBefore().getClassName();
    String classNameAfter = ref.getOperationAfter().getClassName();

    return info.setGroup(Group.METHOD)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setNameBefore(StringUtils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(StringUtils.calculateSignature(ref.getOperationAfter()))
        .setElementAfter(null)
        .setElementBefore(ref.getAnnotation().toString() + " removed from "
            + ref.getVariableAfter().getVariableDeclaration().getVariableName())
        .addMarking(new CodeRange(ref.getOperationBefore().codeRange()),
            new CodeRange(ref.getOperationAfter().codeRange()),
            line -> line.addOffset(
                new LocationInfo(ref.getAnnotation().getLocationInfo()), RefactoringLine.MarkingOption.REMOVE)
                .setHasColumns(false),
            RefactoringLine.MarkingOption.NONE,
            true);
  }

}
