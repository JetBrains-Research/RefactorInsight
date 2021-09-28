package org.jetbrains.research.refactorinsight.data.types.methods;

import gr.uom.java.xmi.diff.ModifyVariableAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

public class ModifyParameterAnnotationJavaHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ModifyVariableAnnotationRefactoring ref = (ModifyVariableAnnotationRefactoring) refactoring;
    String classNameBefore = ref.getOperationBefore().getClassName();
    String classNameAfter = ref.getOperationAfter().getClassName();

    return info.setGroup(Group.METHOD)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setNameBefore(StringUtils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(StringUtils.calculateSignature(ref.getOperationAfter()))
        .setElementAfter(ref.getAnnotationAfter().toString() + " for parameter "
            + ref.getVariableAfter().getVariableName())
        .setElementBefore(ref.getAnnotationBefore().toString())
        .addMarking(new CodeRange(ref.getAnnotationBefore().codeRange()),
            new CodeRange(ref.getAnnotationAfter().codeRange()), true);
  }

}
