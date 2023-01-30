package org.jetbrains.research.refactorinsight.data.attributes;

import gr.uom.java.xmi.diff.MoveAttributeRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;

public class MoveAttributeJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        MoveAttributeRefactoring ref = (MoveAttributeRefactoring) refactoring;

        String classNameBefore = ref.getSourceClassName();
        String classNameAfter = ref.getTargetClassName();

        return info.setGroup(Group.ATTRIBUTE)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(ref.getOriginalAttribute().getVariableDeclaration().toQualifiedString())
                .setNameAfter(ref.getMovedAttribute().getVariableDeclaration().toQualifiedString())
                .addMarking(createCodeRangeFromJava(ref.getOriginalAttribute().codeRange()),
                        createCodeRangeFromJava(ref.getMovedAttribute().codeRange()),
                        true);
    }

}
