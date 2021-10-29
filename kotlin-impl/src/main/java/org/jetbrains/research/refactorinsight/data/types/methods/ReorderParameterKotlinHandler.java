package org.jetbrains.research.refactorinsight.data.types.methods;

import org.jetbrains.research.kotlinrminer.api.Refactoring;
import org.jetbrains.research.kotlinrminer.decomposition.VariableDeclaration;
import org.jetbrains.research.kotlinrminer.diff.refactoring.ReorderParameterRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;

import java.util.List;
import java.util.stream.IntStream;

public class ReorderParameterKotlinHandler extends Handler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring,
                                   RefactoringInfo info) {
        ReorderParameterRefactoring ref =
                (ReorderParameterRefactoring) refactoring;

        String classNameBefore = ref.getOperationBefore().getClassName();
        String classNameAfter = ref.getOperationAfter().getClassName();
        List<org.jetbrains.research.kotlinrminer.decomposition.VariableDeclaration> befores = ref.getParametersBefore();
        List<org.jetbrains.research.kotlinrminer.decomposition.VariableDeclaration> afters = ref.getParametersAfter();

        IntStream.range(0, Math.min(befores.size(), afters.size()))
                .mapToObj(i -> new Pair(befores.get(i), afters.get(i)))
                .forEach(x -> {
                    if (!x.first.getVariableDeclaration().getType()
                            .equals(x.second.getVariableDeclaration().getType())
                            || !x.first.getVariableName().equals(x.second.getVariableName())) {
                        info.addMarking(CodeRange.createCodeRangeFromKotlin(x.first.codeRange()),
                                CodeRange.createCodeRangeFromKotlin(x.second.codeRange()),
                                true);
                    }
                });

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(StringUtils.calculateSignatureForKotlinMethod(ref.getOperationBefore()))
                .setNameAfter(StringUtils.calculateSignatureForKotlinMethod(ref.getOperationAfter()));
    }

    private class Pair {
        private final VariableDeclaration first;
        private final VariableDeclaration second;

        Pair(VariableDeclaration first, VariableDeclaration second) {
            this.first = first;
            this.second = second;
        }
    }
}
