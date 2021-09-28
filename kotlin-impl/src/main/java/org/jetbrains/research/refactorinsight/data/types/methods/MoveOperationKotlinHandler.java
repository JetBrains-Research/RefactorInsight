package org.jetbrains.research.refactorinsight.data.types.methods;

import org.jetbrains.kotlin.com.intellij.lang.folding.FoldingBuilder;
import org.jetbrains.research.kotlinrminer.api.Refactoring;
import org.jetbrains.research.kotlinrminer.api.RefactoringType;
import org.jetbrains.research.kotlinrminer.diff.refactoring.MoveOperationRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;
import org.jetbrains.research.refactorinsight.common.utils.Utils;

import java.util.List;

public class MoveOperationKotlinHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring,
                                 RefactoringInfo info) {
    final MoveOperationRefactoring ref =
        (MoveOperationRefactoring) refactoring;

    info.setFoldingDescriptorBefore(FoldingBuilder.fromMethod(ref.getOriginalOperation()));
    info.setFoldingDescriptorAfter(FoldingBuilder.fromMethod(ref.getMovedOperation()));

    if (info.getType() != RefactoringType.MOVE_AND_RENAME_OPERATION) {
      List<org.jetbrains.research.kotlinrminer.decomposition.AbstractStatement> statementsBefore =
          ref.getOriginalOperation().getBody().getCompositeStatement().getStatements();
      List<org.jetbrains.research.kotlinrminer.decomposition.AbstractStatement> statementsAfter =
          ref.getMovedOperation().getBody().getCompositeStatement().getStatements();
      info.setChanged(!Utils.isStatementsEqualKotlin(statementsBefore, statementsAfter));
    }

    String classBefore = ref.getOriginalOperation().getClassName();
    String classAfter = ref.getMovedOperation().getClassName();

    return info.setGroup(Group.METHOD)
        .setDetailsBefore(classBefore)
        .setDetailsAfter(classAfter)
        .addMarking(
            new CodeRange(ref.getOriginalOperation().codeRange()),
            new CodeRange(ref.getMovedOperation().codeRange()),
            refactoringLine -> refactoringLine.setWord(new String[]{
                ref.getOriginalOperation().getName(),
                null,
                ref.getMovedOperation().getName()
            }),
            RefactoringLine.MarkingOption.COLLAPSE,
            true)
        .setNameBefore(StringUtils.calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(StringUtils.calculateSignature(ref.getMovedOperation()));
  }

}