package org.jetbrains.research.refactorinsight.data.types.methods;

import gr.uom.java.xmi.diff.MoveOperationRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.jetbrains.research.refactorinsight.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

public class MoveOperationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    final MoveOperationRefactoring ref = (MoveOperationRefactoring) refactoring;

    String classBefore = ref.getOriginalOperation().getClassName();
    String classAfter = ref.getMovedOperation().getClassName();

    return info.setGroup(Group.METHOD)
        .setDetailsBefore(classBefore)
        .setDetailsAfter(classAfter)
        .addMarking(
            ref.getOriginalOperation().codeRange(),
            ref.getMovedOperation().codeRange(),
            refactoringLine -> {
              refactoringLine.setWord(new String[] {
                  ref.getOriginalOperation().getName(),
                  null,
                  ref.getMovedOperation().getName()
              });
            },
            RefactoringLine.MarkingOption.COLLAPSE,
            true)
        .setNameBefore(StringUtils.calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(StringUtils.calculateSignature(ref.getMovedOperation()));
  }
}