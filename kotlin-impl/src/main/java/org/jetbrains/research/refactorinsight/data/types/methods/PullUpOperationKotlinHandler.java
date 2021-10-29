package org.jetbrains.research.refactorinsight.data.types.methods;

import org.jetbrains.research.kotlinrminer.api.Refactoring;
import org.jetbrains.research.kotlinrminer.diff.refactoring.PullUpOperationRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;
import org.jetbrains.research.refactorinsight.common.utils.Utils;

import java.util.List;

public class PullUpOperationKotlinHandler extends Handler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring,
                                   RefactoringInfo info) {
        PullUpOperationRefactoring ref =
                (PullUpOperationRefactoring) refactoring;

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
                .addMarking(CodeRange.createCodeRangeFromKotlin(ref.getOriginalOperation().codeRange()),
                        CodeRange.createCodeRangeFromKotlin(ref.getMovedOperation().codeRange()),
                        true)
                .setNameBefore(StringUtils.calculateSignatureForKotlinMethod(ref.getOriginalOperation()))
                .setNameAfter(StringUtils.calculateSignatureForKotlinMethod(ref.getMovedOperation()));
    }

}
