package org.jetbrains.research.refactorinsight.kotlin.impl.data.methods;

import org.jetbrains.research.kotlinrminer.ide.Refactoring;
import org.jetbrains.research.kotlinrminer.ide.decomposition.AbstractStatement;
import org.jetbrains.research.kotlinrminer.ide.diff.refactoring.PushDownOperationRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.FoldingBuilder;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.KotlinRefactoringHandler;

import java.util.List;

import static org.jetbrains.research.refactorinsight.kotlin.impl.data.util.KotlinUtils.*;

public class PushDownOperationKotlinHandler extends KotlinRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring,
                                   RefactoringInfo info) {
        PushDownOperationRefactoring ref =
                (PushDownOperationRefactoring) refactoring;

        List<AbstractStatement> statementsBefore =
                ref.getOriginalOperation().getBody().getCompositeStatement().getStatements();
        List<AbstractStatement> statementsAfter =
                ref.getMovedOperation().getBody().getCompositeStatement().getStatements();
        info.setChanged(!isStatementsEqualKotlin(statementsBefore, statementsAfter));

        info.setFoldingDescriptorBefore(FoldingBuilder.fromMethod(ref.getOriginalOperation()));
        info.setFoldingDescriptorAfter(FoldingBuilder.fromMethod(ref.getMovedOperation()));

        String classBefore = ref.getOriginalOperation().getClassName();
        String classAfter = ref.getMovedOperation().getClassName();

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classBefore)
                .setDetailsAfter(classAfter)
                .addMarking(createCodeRangeFromKotlin(ref.getOriginalOperation().codeRange(), info),
                        createCodeRangeFromKotlin(ref.getMovedOperation().codeRange(), info),
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
