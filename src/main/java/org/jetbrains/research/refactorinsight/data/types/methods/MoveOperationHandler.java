package org.jetbrains.research.refactorinsight.data.types.methods;

import gr.uom.java.xmi.decomposition.AbstractStatement;
import gr.uom.java.xmi.diff.MoveOperationRefactoring;
import org.jetbrains.research.refactorinsight.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.jetbrains.research.refactorinsight.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

import java.util.List;

public class MoveOperationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    final MoveOperationRefactoring ref = (MoveOperationRefactoring) refactoring;

    List<AbstractStatement> statementsBefore =
        ref.getOriginalOperation().getBody().getCompositeStatement().getStatements();
    List<AbstractStatement> statementsAfter =
        ref.getOriginalOperation().getBody().getCompositeStatement().getStatements();
    if (statementsBefore.size() == statementsAfter.size()) {
      boolean equal = true;
      for (int i = 0; i < statementsBefore.size() && equal; i++) {
        equal = statementsBefore.get(i).equalFragment(statementsAfter.get(i));
      }
      info.setChanged(!equal);
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

  @Override
  public RefactoringInfo specify(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring,
                                 RefactoringInfo info) {
    final org.jetbrains.research.kotlinrminer.diff.refactoring.MoveOperationRefactoring ref =
        (org.jetbrains.research.kotlinrminer.diff.refactoring.MoveOperationRefactoring) refactoring;

    List<org.jetbrains.research.kotlinrminer.decomposition.AbstractStatement> statementsBefore =
        ref.getOriginalOperation().getBody().getCompositeStatement().getStatements();
    List<org.jetbrains.research.kotlinrminer.decomposition.AbstractStatement> statementsAfter =
        ref.getOriginalOperation().getBody().getCompositeStatement().getStatements();
    if (statementsBefore.size() == statementsAfter.size()) {
      boolean equal = true;
      for (int i = 0; i < statementsBefore.size() && equal; i++) {
        equal = statementsBefore.get(i).equalFragment(statementsAfter.get(i));
      }
      info.setChanged(!equal);
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