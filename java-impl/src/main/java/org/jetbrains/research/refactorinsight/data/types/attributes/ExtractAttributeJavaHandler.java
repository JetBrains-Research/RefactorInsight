package org.jetbrains.research.refactorinsight.data.types.attributes;

import gr.uom.java.xmi.diff.ExtractAttributeRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

public class ExtractAttributeJavaHandler extends Handler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ExtractAttributeRefactoring ref = (ExtractAttributeRefactoring) refactoring;
        ref.leftSide().forEach(extraction ->
                info.addMarking(
                        CodeRange.createCodeRangeFromJava(extraction),
                        CodeRange.createCodeRangeFromJava(ref.getExtractedVariableDeclarationCodeRange()),
                        true
                ));

        return info.setGroup(Group.ATTRIBUTE)
                .setDetailsBefore(ref.getOriginalClass().getName())
                .setDetailsAfter(ref.getNextClass().getName())
                .setNameBefore(ref.getVariableDeclaration().getName() + " : " + ref.getVariableDeclaration().getType())
                .setNameAfter(ref.getVariableDeclaration().getName() + " : "
                        + ref.getVariableDeclaration().getType());
    }

}
