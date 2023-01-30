package org.jetbrains.research.refactorinsight.kotlin.impl.data.methods;

import org.jetbrains.research.kotlinrminer.ide.Refactoring;
import org.jetbrains.research.kotlinrminer.ide.decomposition.AbstractStatement;
import org.jetbrains.research.kotlinrminer.ide.diff.refactoring.MoveOperationRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.KotlinRefactoringHandler;

import java.util.List;

import static org.jetbrains.research.refactorinsight.kotlin.impl.data.util.KotlinUtils.*;

public class MoveOperationKotlinHandler extends KotlinRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring,
                                   RefactoringInfo info) {
        final MoveOperationRefactoring ref =
                (MoveOperationRefactoring) refactoring;

        if (!info.getType().equals("Move And Rename Method")) {
            List<AbstractStatement> statementsBefore =
                    ref.getOriginalOperation().getBody().getCompositeStatement().getStatements();
            List<AbstractStatement> statementsAfter =
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