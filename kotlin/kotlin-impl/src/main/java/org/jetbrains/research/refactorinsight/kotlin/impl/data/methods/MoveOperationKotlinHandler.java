package org.jetbrains.research.refactorinsight.kotlin.impl.data.methods;

import org.jetbrains.research.kotlinrminer.api.Refactoring;
import org.jetbrains.research.kotlinrminer.diff.refactoring.MoveOperationRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.KotlinRefactoringHandler;

import java.util.List;

import static org.jetbrains.research.refactorinsight.kotlin.impl.data.util.Utils.*;

public class MoveOperationKotlinHandler extends KotlinRefactoringHandler {

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
            info.setChanged(!isStatementsEqualKotlin(statementsBefore, statementsAfter));
        }

        String classBefore = ref.getOriginalOperation().getClassName();
        String classAfter = ref.getMovedOperation().getClassName();

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classBefore)
                .setDetailsAfter(classAfter)
                .addMarking(createCodeRangeFromKotlin(ref.getOriginalOperation().codeRange()),
                        createCodeRangeFromKotlin(ref.getMovedOperation().codeRange()),
                        refactoringLine -> refactoringLine.setWord(new String[]{
                                ref.getOriginalOperation().getName(),
                                null,
                                ref.getMovedOperation().getName()
                        }),
                        RefactoringLine.MarkingOption.COLLAPSE,
                        true)
                .setNameBefore(calculateSignatureForKotlinMethod(ref.getOriginalOperation()))
                .setNameAfter(calculateSignatureForKotlinMethod(ref.getMovedOperation()));
    }

}