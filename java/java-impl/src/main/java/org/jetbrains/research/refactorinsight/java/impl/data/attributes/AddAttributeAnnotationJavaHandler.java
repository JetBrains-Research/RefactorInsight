package org.jetbrains.research.refactorinsight.java.impl.data.attributes;

import gr.uom.java.xmi.UMLAnnotation;
import gr.uom.java.xmi.diff.AddAttributeAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.java.api.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.java.api.util.Utils.createCodeRangeFromJava;
import static org.jetbrains.research.refactorinsight.java.api.util.Utils.createLocationInfoFromJava;

public class AddAttributeAnnotationJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        AddAttributeAnnotationRefactoring ref = (AddAttributeAnnotationRefactoring) refactoring;
        UMLAnnotation annotation = ref.getAnnotation();

        String classNameBefore = ref.getAttributeBefore().getClassName();
        String classNameAfter = ref.getAttributeAfter().getClassName();

        return info.setGroup(Group.ATTRIBUTE)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(ref.getAttributeBefore().getVariableDeclaration().toQualifiedString())
                .setNameAfter(ref.getAttributeAfter().getVariableDeclaration().toQualifiedString())
                .setElementBefore(ref.getAnnotation().toString())
                .setElementAfter(null)
                .addMarking(createCodeRangeFromJava(ref.getAttributeBefore().codeRange()),
                        createCodeRangeFromJava(annotation.codeRange()),
                        line -> line.addOffset(createLocationInfoFromJava(annotation.getLocationInfo()),
                                RefactoringLine.MarkingOption.ADD),
                        RefactoringLine.MarkingOption.ADD,
                        false);
    }

}
