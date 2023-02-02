package org.jetbrains.research.refactorinsight.data.classes;

import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.diff.SplitClassRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

import java.util.stream.Collectors;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;

public class SplitClassJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        SplitClassRefactoring ref = (SplitClassRefactoring) refactoring;

        if (ref.getMovedClass().isAbstract()) {
            info.setGroup(Group.ABSTRACT);
        } else if (ref.getMovedClass().isInterface()) {
            info.setGroup(Group.INTERFACE);
        } else {
            info.setGroup(Group.CLASS);
        }

        ref.getSplitClasses().forEach(clss ->
                info.addMarking(createCodeRangeFromJava(ref.getOriginalClass().codeRange()),
                        createCodeRangeFromJava(clss.codeRange()),
                        true));

        return info
                .setDetailsBefore(ref.getOriginalClass().getPackageName())
                .setDetailsAfter(ref.getMovedClass().getPackageName())
                .setNameBefore(ref.getOriginalClass().getName())
                .setNameAfter(ref.getSplitClasses().stream().map(UMLClass::getName).collect(Collectors.joining()));
    }
    
}
