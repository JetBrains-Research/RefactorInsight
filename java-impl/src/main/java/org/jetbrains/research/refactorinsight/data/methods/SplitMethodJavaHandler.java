package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.VariableDeclarationContainer;
import gr.uom.java.xmi.decomposition.VariableDeclaration;
import gr.uom.java.xmi.diff.SplitOperationRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

import java.util.stream.Collectors;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;

public class SplitMethodJavaHandler extends JavaRefactoringHandler {
    @Override
    protected RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        SplitOperationRefactoring ref = (SplitOperationRefactoring) refactoring;

        ref.getSplitMethods().forEach(method ->
                info.addMarking(createCodeRangeFromJava(ref.getOriginalMethodBeforeSplit().codeRange()),
                        createCodeRangeFromJava(method.codeRange()), true));

        return info.setGroup(Group.METHOD)
                .setNameBefore(ref.getClassNameBefore())
                .setNameAfter(ref.getClassNameAfter())
                .setElementBefore(ref.getOriginalMethodBeforeSplit().toQualifiedString())
                .setElementAfter(ref.getSplitMethods().stream()
                        .map(VariableDeclarationContainer::toQualifiedString)
                        .collect(Collectors.joining()));
    }
}
