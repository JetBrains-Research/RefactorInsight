package org.jetbrains.research.refactorinsight.data.types.methods;

import gr.uom.java.xmi.decomposition.AbstractStatement;
import gr.uom.java.xmi.diff.MoveOperationRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;
import org.jetbrains.research.refactorinsight.common.utils.Utils;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

import java.util.List;
import java.util.Objects;

public class MoveOperationJavaHandler extends Handler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        final MoveOperationRefactoring ref = (MoveOperationRefactoring) refactoring;

        if (!Objects.equals(info.getType(), RefactoringType.MOVE_AND_RENAME_OPERATION.name())) {
            List<AbstractStatement> statementsBefore =
                    ref.getOriginalOperation().getBody().getCompositeStatement().getStatements();
            List<AbstractStatement> statementsAfter =
                    ref.getMovedOperation().getBody().getCompositeStatement().getStatements();
            info.setChanged(!Utils.isStatementsEqualJava(statementsBefore, statementsAfter));
        }

        String classBefore = ref.getOriginalOperation().getClassName();
        String classAfter = ref.getMovedOperation().getClassName();

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classBefore)
                .setDetailsAfter(classAfter)
                .addMarking(
                        CodeRange.createCodeRangeFromJava(ref.getOriginalOperation().codeRange()),
                        CodeRange.createCodeRangeFromJava(ref.getMovedOperation().codeRange()),
                        refactoringLine -> refactoringLine.setWord(new String[]{
                                ref.getOriginalOperation().getName(),
                                null,
                                ref.getMovedOperation().getName()
                        }),
                        RefactoringLine.MarkingOption.COLLAPSE,
                        true)
                .setNameBefore(StringUtils.calculateSignatureForJavaMethod(ref.getOriginalOperation()))
                .setNameAfter(StringUtils.calculateSignatureForJavaMethod(ref.getMovedOperation()));
    }

}