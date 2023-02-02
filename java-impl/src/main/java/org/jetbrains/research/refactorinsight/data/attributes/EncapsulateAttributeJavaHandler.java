package org.jetbrains.research.refactorinsight.data.attributes;

import static org.jetbrains.research.refactorinsight.data.RefactoringLine.MarkingOption.ADD;
import static org.jetbrains.research.refactorinsight.data.RefactoringLine.MarkingOption.NONE;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createLocationInfoFromJava;

import gr.uom.java.xmi.diff.EncapsulateAttributeRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

public class EncapsulateAttributeJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        EncapsulateAttributeRefactoring ref = (EncapsulateAttributeRefactoring) refactoring;

        String classNameBefore = ref.getAttributeBefore().getClassName();
        String classNameAfter = ref.getAttributeAfter().getClassName();

        return info.setGroup(Group.ATTRIBUTE)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(ref.getAttributeBefore().getVariableDeclaration().toQualifiedString())
                .setNameAfter(ref.getAttributeAfter().getVariableDeclaration().toQualifiedString())
                .addMarking(
                        createCodeRangeFromJava(ref.getAttributeBefore().codeRange()),
                        createCodeRangeFromJava(ref.getAttributeAfter().codeRange()),
                        true)
                .addMarking(
                        createCodeRangeFromJava(ref.getAttributeBefore().codeRange()),
                        createCodeRangeFromJava(ref.getAddedSetter().codeRange()),
                        line -> line.addOffset(createLocationInfoFromJava(ref.getAddedSetter().getLocationInfo()), ADD),
                        NONE,
                        true)
                .addMarking(
                        createCodeRangeFromJava(ref.getAttributeBefore().codeRange()),
                        createCodeRangeFromJava(ref.getAddedGetter().codeRange()),
                        line -> line.addOffset(createLocationInfoFromJava(ref.getAddedGetter().getLocationInfo()), ADD),
                        NONE,
                        true);
    }

}
