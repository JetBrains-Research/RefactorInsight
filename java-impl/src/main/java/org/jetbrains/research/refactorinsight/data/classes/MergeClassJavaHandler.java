package org.jetbrains.research.refactorinsight.data.classes;

import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.diff.MergeClassRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

import java.util.stream.Collectors;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;

public class MergeClassJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        MergeClassRefactoring ref = (MergeClassRefactoring) refactoring;

        if (ref.getNewClass().isAbstract()) {
            info.setGroup(Group.ABSTRACT);
        } else if (ref.getNewClass().isInterface()) {
            info.setGroup(Group.INTERFACE);
        } else {
            info.setGroup(Group.CLASS);
        }

        ref.getMergedClasses().forEach(clss ->
                info.addMarking(createCodeRangeFromJava(clss.codeRange()), createCodeRangeFromJava(ref.getNewClass().codeRange()),
                        true));

        return info
                .setDetailsBefore(ref.getOriginalClass().getPackageName())
                .setDetailsAfter(ref.getNewClass().getPackageName())
                .setNameBefore(ref.getMergedClasses().stream().map(UMLClass::getName).collect(Collectors.joining()))
                .setNameAfter(ref.getNewClass().getName());
    }
    
}
