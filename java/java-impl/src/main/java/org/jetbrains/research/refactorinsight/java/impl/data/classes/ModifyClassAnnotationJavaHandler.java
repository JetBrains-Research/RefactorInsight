package org.jetbrains.research.refactorinsight.java.impl.data.classes;

import gr.uom.java.xmi.diff.ModifyClassAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.java.impl.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.java.impl.data.util.JavaUtils.createCodeRangeFromJava;
import static org.jetbrains.research.refactorinsight.java.impl.data.util.JavaUtils.createLocationInfoFromJava;

public class ModifyClassAnnotationJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ModifyClassAnnotationRefactoring ref = (ModifyClassAnnotationRefactoring) refactoring;

        if (ref.getClassAfter().isInterface()) {
            info.setGroup(Group.INTERFACE);
        } else if (ref.getClassAfter().isAbstract()) {
            info.setGroup(Group.ABSTRACT);
        } else {
            info.setGroup(Group.CLASS);
        }

        return info
                .setDetailsBefore(ref.getClassBefore().getPackageName())
                .setDetailsAfter(ref.getClassAfter().getPackageName())
                .setNameBefore(ref.getClassBefore().getName())
                .setNameAfter(ref.getClassAfter().getName())
                .setElementBefore(ref.getAnnotationBefore().toString())
                .setElementAfter(ref.getAnnotationAfter().toString())
                .addMarking(createCodeRangeFromJava(ref.getAnnotationBefore().codeRange()),
                        createCodeRangeFromJava(ref.getAnnotationAfter().codeRange()),
                        line -> line.addOffset(createLocationInfoFromJava(ref.getAnnotationBefore().getLocationInfo()),
                                createLocationInfoFromJava(ref.getAnnotationAfter().getLocationInfo())),
                        RefactoringLine.MarkingOption.NONE, true);
    }

}