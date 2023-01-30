package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.decomposition.VariableDeclaration;
import gr.uom.java.xmi.diff.ReorderParameterRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.util.JavaUtils;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import java.util.List;
import java.util.stream.IntStream;

public class ReorderParameterJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ReorderParameterRefactoring ref = (ReorderParameterRefactoring) refactoring;

        String classNameBefore = ref.getOperationBefore().getClassName();
        String classNameAfter = ref.getOperationAfter().getClassName();
        List<VariableDeclaration> befores = ref.getParametersBefore();
        List<VariableDeclaration> afters = ref.getParametersAfter();

        IntStream.range(0, Math.min(befores.size(), afters.size()))
                .mapToObj(i -> new Pair(befores.get(i), afters.get(i)))
                .forEach(x -> {
                    if (!x.first.getVariableDeclaration().getType()
                            .equals(x.second.getVariableDeclaration().getType())
                            || !x.first.getVariableName().equals(x.second.getVariableName())) {
                        info.addMarking(JavaUtils.createCodeRangeFromJava(x.first.codeRange()),
                                JavaUtils.createCodeRangeFromJava(x.second.codeRange()),
                                true);
                    }
                });

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(JavaUtils.calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(JavaUtils.calculateSignatureForJavaMethod(ref.getOperationAfter()));
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
