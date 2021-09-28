package org.jetbrains.research.refactorinsight.data.types.methods;

import gr.uom.java.xmi.decomposition.AbstractStatement;
import gr.uom.java.xmi.diff.PullUpOperationRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;
import org.jetbrains.research.refactorinsight.common.utils.Utils;
import org.refactoringminer.api.Refactoring;

import java.util.List;

public class PullUpOperationJavaHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    PullUpOperationRefactoring ref = (PullUpOperationRefactoring) refactoring;

    info.setFoldingDescriptorBefore(FoldingBuilder.fromMethod(ref.getOriginalOperation()));
    info.setFoldingDescriptorAfter(FoldingBuilder.fromMethod(ref.getMovedOperation()));

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

}
