package org.jetbrains.research.refactorinsight.data.attributes;

import gr.uom.java.xmi.diff.ModifyAttributeAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createLocationInfoFromJava;

public class ModifyAttributeAnnotationJavaHandler extends JavaRefactoringHandler {
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
                .addMarking(createCodeRangeFromJava(ref.getAnnotationBefore().codeRange()),
                        createCodeRangeFromJava(ref.getAnnotationAfter().codeRange()),
                        line -> line.addOffset(createLocationInfoFromJava(ref.getAnnotationBefore().getLocationInfo()),
                                createLocationInfoFromJava(ref.getAnnotationAfter().getLocationInfo())),
                        RefactoringLine.MarkingOption.NONE, true);
    }

}
