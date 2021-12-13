package org.jetbrains.research.refactorinsight.java.impl.data.attributes;

import gr.uom.java.xmi.decomposition.VariableDeclaration;
import gr.uom.java.xmi.diff.MergeAttributeRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.java.impl.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import java.util.stream.Collectors;

import static org.jetbrains.research.refactorinsight.java.impl.data.util.JavaUtils.createCodeRangeFromJava;

public class MergeAttributeJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        MergeAttributeRefactoring ref = (MergeAttributeRefactoring) refactoring;

        String classNameAfter = ref.getClassNameAfter();
        String classNameBefore = ref.getClassNameBefore();

        ref.getMergedAttributes().forEach(attr ->
                info.addMarking(createCodeRangeFromJava(attr.codeRange()),
                        createCodeRangeFromJava(ref.getNewAttribute().codeRange()),
                        true));

        return info.setGroup(Group.ATTRIBUTE)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(ref.getMergedAttributes().stream()
                        .map(VariableDeclaration::getVariableName)
                        .collect(Collectors.joining()))
                .setNameAfter(ref.getNewAttribute().getVariableDeclaration().getVariableName());

    }

}
