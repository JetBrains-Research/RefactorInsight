package org.jetbrains.research.refactorinsight.data.types.attributes;

import gr.uom.java.xmi.diff.MoveAndRenameAttributeRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

public class MoveRenameAttributeJavaHandler extends Handler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        MoveAndRenameAttributeRefactoring ref = (MoveAndRenameAttributeRefactoring) refactoring;

        String classNameBefore = ref.getSourceClassName();
        String classNameAfter = ref.getTargetClassName();

        return info.setGroup(Group.ATTRIBUTE)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(ref.getOriginalAttribute().getVariableDeclaration().toQualifiedString())
                .setNameAfter(ref.getMovedAttribute().getVariableDeclaration().toQualifiedString())
                .addMarking(CodeRange.createCodeRangeFromJava(ref.getSourceAttributeCodeRangeBeforeMove()),
                        CodeRange.createCodeRangeFromJava(ref.getTargetAttributeCodeRangeAfterMove()),
                        true);
    }

}
