package org.jetbrains.research.refactorinsight.kotlin.impl.data.methods;

import org.jetbrains.research.kotlinrminer.api.Refactoring;
import org.jetbrains.research.kotlinrminer.decomposition.VariableDeclaration;
import org.jetbrains.research.kotlinrminer.diff.refactoring.ReorderParameterRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.KotlinRefactoringHandler;

import java.util.List;
import java.util.stream.IntStream;

import static org.jetbrains.research.refactorinsight.kotlin.impl.data.util.Utils.calculateSignatureForKotlinMethod;
import static org.jetbrains.research.refactorinsight.kotlin.impl.data.util.Utils.createCodeRangeFromKotlin;

public class ReorderParameterKotlinHandler extends KotlinRefactoringHandler {

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
                        info.addMarking(createCodeRangeFromKotlin(x.first.codeRange()),
                                createCodeRangeFromKotlin(x.second.codeRange()),
                                true);
                    }
                });

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(calculateSignatureForKotlinMethod(ref.getOperationBefore()))
                .setNameAfter(calculateSignatureForKotlinMethod(ref.getOperationAfter()));
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
