package org.jetbrains.research.refactorinsight.data.types.methods;

import gr.uom.java.xmi.decomposition.AbstractStatement;
import gr.uom.java.xmi.diff.PullUpOperationRefactoring;
import org.jetbrains.research.refactorinsight.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.jetbrains.research.refactorinsight.folding.FoldingPositions;
import org.jetbrains.research.refactorinsight.utils.StringUtils;
import org.jetbrains.research.refactorinsight.utils.Utils;
import org.refactoringminer.api.Refactoring;

import java.util.List;

public class PullUpOperationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    PullUpOperationRefactoring ref = (PullUpOperationRefactoring) refactoring;

    info.setFoldingPositionsBefore(FoldingPositions.fromMethod(ref.getOriginalOperation()));
    info.setFoldingPositionsAfter(FoldingPositions.fromMethod(ref.getMovedOperation()));

    List<AbstractStatement> statementsBefore =
        ref.getOriginalOperation().getBody().getCompositeStatement().getStatements();
    List<AbstractStatement> statementsAfter =
        ref.getMovedOperation().getBody().getCompositeStatement().getStatements();
    info.setChanged(!Utils.isStatementsEqualJava(statementsBefore, statementsAfter));

    String classBefore = ref.getOriginalOperation().getClassName();
    String classAfter = ref.getMovedOperation().getClassName();

    return info.setGroup(Group.METHOD)
        .setDetailsBefore(classBefore)
        .setDetailsAfter(classAfter)
        .addMarking(
            new CodeRange(ref.getOriginalOperation().codeRange()),
            new CodeRange(ref.getMovedOperation().codeRange()),
            true)
        .setNameBefore(StringUtils.calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(StringUtils.calculateSignature(ref.getMovedOperation()));
  }

  @Override
  public RefactoringInfo specify(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring,
                                 RefactoringInfo info) {
    org.jetbrains.research.kotlinrminer.diff.refactoring.PullUpOperationRefactoring ref =
        (org.jetbrains.research.kotlinrminer.diff.refactoring.PullUpOperationRefactoring) refactoring;

    info.setFoldingPositionsBefore(FoldingPositions.fromMethod(ref.getOriginalOperation()));
    info.setFoldingPositionsAfter(FoldingPositions.fromMethod(ref.getMovedOperation()));

    List<org.jetbrains.research.kotlinrminer.decomposition.AbstractStatement> statementsBefore =
        ref.getOriginalOperation().getBody().getCompositeStatement().getStatements();
    List<org.jetbrains.research.kotlinrminer.decomposition.AbstractStatement> statementsAfter =
        ref.getMovedOperation().getBody().getCompositeStatement().getStatements();
    info.setChanged(!Utils.isStatementsEqualKotlin(statementsBefore, statementsAfter));

    String classBefore = ref.getOriginalOperation().getClassName();
    String classAfter = ref.getMovedOperation().getClassName();

    return info.setGroup(Group.METHOD)
        .setDetailsBefore(classBefore)
        .setDetailsAfter(classAfter)
        .addMarking(
            new CodeRange(ref.getOriginalOperation().codeRange()),
            new CodeRange(ref.getMovedOperation().codeRange()),
            true)
        .setNameBefore(StringUtils.calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(StringUtils.calculateSignature(ref.getMovedOperation()));
  }
}
