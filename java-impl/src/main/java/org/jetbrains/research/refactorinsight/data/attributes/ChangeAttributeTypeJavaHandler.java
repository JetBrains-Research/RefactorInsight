package org.jetbrains.research.refactorinsight.data.attributes;

import gr.uom.java.xmi.diff.ChangeAttributeTypeRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;

public class ChangeAttributeTypeJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ChangeAttributeTypeRefactoring ref = (ChangeAttributeTypeRefactoring) refactoring;

        String classNameBefore = ref.getClassNameBefore();
        String classNameAfter = ref.getClassNameAfter();

        return info.setGroup(Group.ATTRIBUTE)
                .setGroupId(ref.getClassNameAfter() + "." + ref.getChangedTypeAttribute().getVariableDeclaration().getVariableName())
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(ref.getOriginalAttribute().getVariableDeclaration().toQualifiedString())
                .setNameAfter(ref.getChangedTypeAttribute().getVariableDeclaration().toQualifiedString())
                .addMarking(createCodeRangeFromJava(ref.getOriginalAttribute().getType().codeRange()),
                        createCodeRangeFromJava(ref.getChangedTypeAttribute().getType().codeRange()),
                        true);
    }

}
