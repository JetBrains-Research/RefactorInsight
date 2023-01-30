package org.jetbrains.research.refactorinsight.kotlin.impl.data.methods;

import org.jetbrains.research.kotlinrminer.ide.Refactoring;
import org.jetbrains.research.kotlinrminer.ide.decomposition.AbstractStatement;
import org.jetbrains.research.kotlinrminer.ide.decomposition.OperationBody;
import org.jetbrains.research.kotlinrminer.ide.diff.refactoring.PullUpOperationRefactoring;
import org.jetbrains.research.kotlinrminer.ide.uml.UMLOperation;
import org.jetbrains.research.refactorinsight.RefactoringProcessingException;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.KotlinRefactoringHandler;

import java.util.List;

import static org.jetbrains.research.refactorinsight.kotlin.impl.data.util.KotlinUtils.*;

public class PullUpOperationKotlinHandler extends KotlinRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring,
                                   RefactoringInfo info) throws RefactoringProcessingException {
        PullUpOperationRefactoring ref =
                (PullUpOperationRefactoring) refactoring;

        UMLOperation originalOperation = ref.getOriginalOperation();
        if (originalOperation == null || originalOperation.getBody() == null) {
            throw new RefactoringProcessingException("Error occurred while processing Kotlin Pull Up operation refactoring.");
        }

        OperationBody originalOperationBody = originalOperation.getBody();
        List<AbstractStatement> statementsBefore =
                originalOperationBody.getCompositeStatement().getStatements();
        List<AbstractStatement> statementsAfter =
                ref.getMovedOperation().getBody().getCompositeStatement().getStatements();
        info.setChanged(!isStatementsEqualKotlin(statementsBefore, statementsAfter));

        String classBefore = originalOperation.getClassName();
        String classAfter = ref.getMovedOperation().getClassName();

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classBefore)
                .setDetailsAfter(classAfter)
                .addMarking(createCodeRangeFromKotlin(originalOperation.codeRange()),
                        createCodeRangeFromKotlin(ref.getMovedOperation().codeRange()),
                        true)
                .setNameBefore(calculateSignatureForKotlinMethod(originalOperation))
                .setNameAfter(calculateSignatureForKotlinMethod(ref.getMovedOperation()));
    }

}
