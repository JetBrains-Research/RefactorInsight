package org.jetbrains.research.refactorinsight.data.types.methods;

import gr.uom.java.xmi.diff.ModifyMethodAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.jetbrains.research.refactorinsight.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

public class ModifyMethodAnnotationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ModifyMethodAnnotationRefactoring ref = (ModifyMethodAnnotationRefactoring) refactoring;

    String classNameBefore = ref.getOperationBefore().getClassName();
    String classNameAfter = ref.getOperationAfter().getClassName();

    return info.setGroup(Group.METHOD)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setElementBefore(ref.getAnnotationBefore().toString())
        .setElementAfter(ref.getAnnotationAfter().toString())
        .addMarking(new CodeRange(ref.getAnnotationBefore().codeRange()),
            new CodeRange(ref.getAnnotationAfter().codeRange()),
            true)
        .setNameBefore(StringUtils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(StringUtils.calculateSignature(ref.getOperationAfter()));
  }

  @Override
  public RefactoringInfo specify(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring,
                                 RefactoringInfo info) {
    //This kind of refactoring is not supported by kotlinRMiner yet.
    return null;
  }
}
