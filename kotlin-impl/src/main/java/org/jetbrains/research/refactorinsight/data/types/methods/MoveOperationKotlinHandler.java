package org.jetbrains.research.refactorinsight.data.types.methods;

import org.jetbrains.research.kotlinrminer.api.Refactoring;
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

        if (!info.getType().equals("Move And Rename Method")) {
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
                .addMarking(CodeRange.createCodeRangeFromKotlin(ref.getOriginalOperation().codeRange()),
                        CodeRange.createCodeRangeFromKotlin(ref.getMovedOperation().codeRange()),
                        refactoringLine -> refactoringLine.setWord(new String[]{
                                ref.getOriginalOperation().getName(),
                                null,
                                ref.getMovedOperation().getName()
                        }),
                        RefactoringLine.MarkingOption.COLLAPSE,
                        true)
                .setNameBefore(StringUtils.calculateSignatureForKotlinMethod(ref.getOriginalOperation()))
                .setNameAfter(StringUtils.calculateSignatureForKotlinMethod(ref.getMovedOperation()));
    }

}