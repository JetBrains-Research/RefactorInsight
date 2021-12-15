package org.jetbrains.research.refactorinsight.java.impl.data.attributes;

import gr.uom.java.xmi.diff.MoveAndRenameAttributeRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.java.impl.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.java.impl.data.util.JavaUtils.createCodeRangeFromJava;

public class MoveRenameAttributeJavaHandler extends JavaRefactoringHandler {

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
                .addMarking(createCodeRangeFromJava(ref.getSourceAttributeCodeRangeBeforeMove()),
                        createCodeRangeFromJava(ref.getTargetAttributeCodeRangeAfterMove()),
                        true);
    }

}