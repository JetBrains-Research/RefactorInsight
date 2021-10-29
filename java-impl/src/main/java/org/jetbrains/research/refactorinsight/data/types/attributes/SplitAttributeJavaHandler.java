package org.jetbrains.research.refactorinsight.data.types.attributes;

import gr.uom.java.xmi.decomposition.VariableDeclaration;
import gr.uom.java.xmi.diff.SplitAttributeRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

import java.util.stream.Collectors;

public class SplitAttributeJavaHandler extends Handler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        SplitAttributeRefactoring ref = (SplitAttributeRefactoring) refactoring;
        ref.getSplitAttributes().forEach(attr ->
                info.addMarking(CodeRange.createCodeRangeFromJava(ref.getOldAttribute().codeRange()),
                        CodeRange.createCodeRangeFromJava(attr.codeRange()),
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