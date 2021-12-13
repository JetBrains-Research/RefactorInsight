package org.jetbrains.research.refactorinsight.java.impl.data.attributes;

import gr.uom.java.xmi.diff.ModifyAttributeAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.java.impl.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.java.impl.data.util.Utils.createCodeRangeFromJava;
import static org.jetbrains.research.refactorinsight.java.impl.data.util.Utils.createLocationInfoFromJava;

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
