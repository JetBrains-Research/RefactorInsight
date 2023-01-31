package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.decomposition.AbstractStatement;
import gr.uom.java.xmi.diff.MoveOperationRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.data.util.JavaUtils;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

import java.util.List;
import java.util.Objects;

public class MoveOperationJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        final MoveOperationRefactoring ref = (MoveOperationRefactoring) refactoring;

        if (!Objects.equals(info.getType(), RefactoringType.MOVE_AND_RENAME_OPERATION.getDisplayName())) {
            List<AbstractStatement> statementsBefore =
                    ref.getOriginalOperation().getBody().getCompositeStatement().getStatements();
            List<AbstractStatement> statementsAfter =
                    ref.getMovedOperation().getBody().getCompositeStatement().getStatements();
            info.setChanged(!JavaUtils.isStatementsEqualJava(statementsBefore, statementsAfter));
        }

        String classBefore = ref.getOriginalOperation().getClassName();
        String classAfter = ref.getMovedOperation().getClassName();

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classBefore)
                .setDetailsAfter(classAfter)
                .addMarking(
                        JavaUtils.createCodeRangeFromJava(ref.getOriginalOperation().codeRange()),
                        JavaUtils.createCodeRangeFromJava(ref.getMovedOperation().codeRange()),
                        refactoringLine -> refactoringLine.setWord(new String[]{
                                ref.getOriginalOperation().getName(),
                                null,
                                ref.getMovedOperation().getName()
                        }),
                        RefactoringLine.MarkingOption.COLLAPSE,
                        true)
                .setNameBefore(JavaUtils.calculateSignatureForJavaMethod(ref.getOriginalOperation()))
                .setNameAfter(JavaUtils.calculateSignatureForJavaMethod(ref.getMovedOperation()));
    }

}