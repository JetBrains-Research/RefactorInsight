package org.jetbrains.research.refactorinsight.data.attributes;

import gr.uom.java.xmi.diff.InlineAttributeRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;

public class InlineAttributeJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        InlineAttributeRefactoring ref = (InlineAttributeRefactoring) refactoring;

        String classNameBefore = ref.getOriginalClass().getName();
        String classNameAfter = ref.getNextClass().getName();

        ref.getReferences().forEach( reference ->
                info.addMarking(
                        createCodeRangeFromJava(ref.getVariableDeclaration().codeRange()),
                        createCodeRangeFromJava(reference.getFragment2().codeRange()),
                        true
                )
        );

        return info.setGroup(Group.ATTRIBUTE)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setElementBefore(ref.getVariableDeclaration().getName())
                .setElementAfter(null)
                .setNameBefore(ref.getVariableDeclaration().toQualifiedString())
                .setNameAfter(ref.getVariableDeclaration().toQualifiedString());
    }

}
