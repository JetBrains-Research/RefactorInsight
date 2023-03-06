package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.VariableDeclarationContainer;
import gr.uom.java.xmi.diff.MergeOperationRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

import java.util.stream.Collectors;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;

public class MergeMethodJavaHandler extends JavaRefactoringHandler {
    @Override
    protected RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        MergeOperationRefactoring ref = (MergeOperationRefactoring) refactoring;

        ref.getMergedMethods().forEach(method ->
                info.addMarking(createCodeRangeFromJava(method.codeRange()),
                        createCodeRangeFromJava(ref.getNewMethodAfterMerge().codeRange()), true));

        return info.setGroup(Group.METHOD)
                .setNameBefore(ref.getClassNameBefore())
                .setNameAfter(ref.getClassNameAfter())
                .setElementBefore(ref.getMergedMethods().stream()
                        .map(VariableDeclarationContainer::toQualifiedString)
                        .collect(Collectors.joining()))
                .setElementAfter(ref.getNewMethodAfterMerge().toQualifiedString());
    }
}
