package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.decomposition.AbstractStatement;
import gr.uom.java.xmi.diff.PullUpOperationRefactoring;
import org.jetbrains.research.refactorinsight.data.FoldingBuilder;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.util.JavaUtils;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import java.util.List;

public class PullUpOperationJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        PullUpOperationRefactoring ref = (PullUpOperationRefactoring) refactoring;

        List<AbstractStatement> statementsBefore =
                ref.getOriginalOperation().getBody().getCompositeStatement().getStatements();
        List<AbstractStatement> statementsAfter =
                ref.getMovedOperation().getBody().getCompositeStatement().getStatements();
        info.setChanged(!JavaUtils.isStatementsEqualJava(statementsBefore, statementsAfter));

        info.setFoldingDescriptorBefore(FoldingBuilder.fromMethod(ref.getOriginalOperation()));
        info.setFoldingDescriptorAfter(FoldingBuilder.fromMethod(ref.getMovedOperation()));

        String classBefore = ref.getOriginalOperation().getClassName();
        String classAfter = ref.getMovedOperation().getClassName();

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classBefore)
                .setDetailsAfter(classAfter)
                .addMarking(JavaUtils.createCodeRangeFromJava(ref.getOriginalOperation().codeRange()),
                        JavaUtils.createCodeRangeFromJava(ref.getMovedOperation().codeRange()),
                        true)
                .setNameBefore(JavaUtils.calculateSignatureForJavaMethod(ref.getOriginalOperation()))
                .setNameAfter(JavaUtils.calculateSignatureForJavaMethod(ref.getMovedOperation()));
    }

}
