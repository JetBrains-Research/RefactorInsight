package org.jetbrains.research.refactorinsight.java.impl.data.attributes;

import gr.uom.java.xmi.diff.PullUpAttributeRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.java.impl.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.java.impl.data.util.Utils.createCodeRangeFromJava;

public class PullUpAttributeJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        PullUpAttributeRefactoring ref = (PullUpAttributeRefactoring) refactoring;

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
