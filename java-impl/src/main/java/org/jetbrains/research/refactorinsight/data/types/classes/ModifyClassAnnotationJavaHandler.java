package org.jetbrains.research.refactorinsight.data.types.classes;

import gr.uom.java.xmi.diff.ModifyClassAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.adapters.LocationInfo;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.refactoringminer.api.Refactoring;

public class ModifyClassAnnotationJavaHandler extends Handler {

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
                .addMarking(CodeRange.createCodeRangeFromJava(ref.getAnnotationBefore().codeRange()),
                        CodeRange.createCodeRangeFromJava(ref.getAnnotationAfter().codeRange()),
                        line -> line.addOffset(LocationInfo.createLocationInfoFromJava(ref.getAnnotationBefore().getLocationInfo()),
                                LocationInfo.createLocationInfoFromJava(ref.getAnnotationAfter().getLocationInfo())),
                        RefactoringLine.MarkingOption.NONE, true);
    }

}