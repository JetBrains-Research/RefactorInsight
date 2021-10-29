package org.jetbrains.research.refactorinsight.data.types.attributes;

import gr.uom.java.xmi.diff.ModifyAttributeAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.adapters.LocationInfo;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.refactoringminer.api.Refactoring;

public class ModifyAttributeAnnotationJavaHandler extends Handler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ModifyAttributeAnnotationRefactoring ref = (ModifyAttributeAnnotationRefactoring) refactoring;

        String classNameBefore = ref.getAttributeBefore().getClassName();
        String classNameAfter = ref.getAttributeAfter().getClassName();

        return info.setGroup(Group.ATTRIBUTE)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(ref.getAttributeBefore().getVariableDeclaration().toQualifiedString())
                .setNameAfter(ref.getAttributeAfter().getVariableDeclaration().toQualifiedString())
                .setElementBefore(ref.getAnnotationBefore().toString())
                .setElementAfter(ref.getAnnotationAfter().toString())
                .addMarking(CodeRange.createCodeRangeFromJava(ref.getAnnotationBefore().codeRange()),
                        CodeRange.createCodeRangeFromJava(ref.getAnnotationAfter().codeRange()),
                        line -> line.addOffset(LocationInfo.createLocationInfoFromJava(ref.getAnnotationBefore().getLocationInfo()),
                                LocationInfo.createLocationInfoFromJava(ref.getAnnotationAfter().getLocationInfo())),
                        RefactoringLine.MarkingOption.NONE, true);
    }

}
