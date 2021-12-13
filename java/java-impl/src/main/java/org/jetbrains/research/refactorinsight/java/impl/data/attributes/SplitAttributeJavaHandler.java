package org.jetbrains.research.refactorinsight.java.impl.data.attributes;

import gr.uom.java.xmi.decomposition.VariableDeclaration;
import gr.uom.java.xmi.diff.SplitAttributeRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.java.impl.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import java.util.stream.Collectors;

import static org.jetbrains.research.refactorinsight.java.impl.data.util.JavaUtils.createCodeRangeFromJava;

public class SplitAttributeJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        SplitAttributeRefactoring ref = (SplitAttributeRefactoring) refactoring;
        ref.getSplitAttributes().forEach(attr ->
                info.addMarking(createCodeRangeFromJava(ref.getOldAttribute().codeRange()),
                        createCodeRangeFromJava(attr.codeRange()),
                        true));

        String classNameBefore = ref.getClassNameBefore();
        String classNameAfter = ref.getClassNameAfter();

        return info.setGroup(Group.ATTRIBUTE)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(ref.getOldAttribute().getVariableName())
                .setNameAfter(ref.getSplitAttributes().stream().map(VariableDeclaration::getVariableName)
                        .collect(Collectors.joining()));
    }

}