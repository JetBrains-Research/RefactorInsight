package org.jetbrains.research.refactorinsight.data.classes;

import gr.uom.java.xmi.diff.ChangeTypeDeclarationKindRefactoring;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;

public class ChangeTypeDeclarationKindJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ChangeTypeDeclarationKindRefactoring ref = (ChangeTypeDeclarationKindRefactoring) refactoring;

        String classNameBefore = ref.getClassBefore().getName();
        String classNameAfter = ref.getClassAfter().getName();

        return info
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(ref.getClassBefore().getName())
                .setNameAfter(ref.getClassAfter().getName())
                .addMarking(createCodeRangeFromJava(ref.getClassBefore().codeRange()),
                        createCodeRangeFromJava(ref.getClassAfter().codeRange()),
                        true);
    }
    
}
