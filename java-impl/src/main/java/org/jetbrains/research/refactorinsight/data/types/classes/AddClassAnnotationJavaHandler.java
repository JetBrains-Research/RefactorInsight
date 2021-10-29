package org.jetbrains.research.refactorinsight.data.types.classes;

import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.AddClassAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.adapters.LocationInfo;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.refactoringminer.api.Refactoring;

public class AddClassAnnotationJavaHandler extends Handler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        AddClassAnnotationRefactoring ref = (AddClassAnnotationRefactoring) refactoring;
        UMLAnnotation annotation = ref.getAnnotation();

        if (ref.getClassAfter().isAbstract()) {
            info.setGroup(Group.ABSTRACT);
        } else if (ref.getClassAfter().isInterface()) {
            info.setGroup(Group.INTERFACE);
        } else {
            info.setGroup(Group.CLASS);
        }

        return info
                .setDetailsBefore(ref.getClassBefore().getPackageName())
                .setDetailsAfter(ref.getClassAfter().getPackageName())
                .setNameBefore(ref.getClassBefore().getName())
                .setNameAfter(ref.getClassAfter().getName())
                .setElementBefore(ref.getAnnotation().toString())
                .setElementAfter(null)
                .addMarking(CodeRange.createCodeRangeFromJava(ref.getClassBefore().codeRange()),
                        CodeRange.createCodeRangeFromJava(annotation.codeRange()),
                        line -> line.addOffset(
                                LocationInfo.createLocationInfoFromJava(annotation.getLocationInfo()),
                                RefactoringLine.MarkingOption.ADD),
                        RefactoringLine.MarkingOption.ADD,
                        false);
    }

}
