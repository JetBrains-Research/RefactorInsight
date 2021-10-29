package org.jetbrains.research.refactorinsight.data.types.attributes;

import gr.uom.java.xmi.decomposition.VariableDeclaration;
import gr.uom.java.xmi.diff.MergeAttributeRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

import java.util.stream.Collectors;

public class MergeAttributeJavaHandler extends Handler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        MergeAttributeRefactoring ref = (MergeAttributeRefactoring) refactoring;

        String classNameAfter = ref.getClassNameAfter();
        String classNameBefore = ref.getClassNameBefore();

        ref.getMergedAttributes().forEach(attr ->
                info.addMarking(CodeRange.createCodeRangeFromJava(attr.codeRange()),
                        CodeRange.createCodeRangeFromJava(ref.getNewAttribute().codeRange()),
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
