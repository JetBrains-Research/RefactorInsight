package org.jetbrains.research.refactorinsight.data.classes;

import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.RemoveClassAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.data.util.JavaUtils;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

public class RemoveClassAnnotationJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        RemoveClassAnnotationRefactoring ref = (RemoveClassAnnotationRefactoring) refactoring;
        UMLAnnotation annotation = ref.getAnnotation();

        if (ref.getClassAfter().isAbstract()) {
            info.setGroup(Group.ABSTRACT);
        } else if (ref.getClassAfter().isInterface()) {
            info.setGroup(Group.INTERFACE);
        } else {
            info.setGroup(Group.CLASS);
        }

        return info.setNameBefore(ref.getClassBefore().getName())
                .setNameAfter(ref.getClassAfter().getName())
                .setElementBefore(ref.getAnnotation().toString())
                .setElementAfter(null)
                .setDetailsBefore(ref.getClassBefore().getPackageName())
                .setDetailsAfter(ref.getClassAfter().getPackageName())
                .addMarking(JavaUtils.createCodeRangeFromJava(annotation.codeRange()),
                        JavaUtils.createCodeRangeFromJava(ref.getClassAfter().codeRange()),
                        line -> line.addOffset(
                                JavaUtils.createLocationInfoFromJava(annotation.getLocationInfo()),
                                RefactoringLine.MarkingOption.REMOVE),
                        RefactoringLine.MarkingOption.REMOVE,
                        false);
    }

}
