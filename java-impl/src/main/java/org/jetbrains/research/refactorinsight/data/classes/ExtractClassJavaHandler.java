package org.jetbrains.research.refactorinsight.data.classes;

import gr.uom.java.xmi.diff.ExtractClassRefactoring;
import org.jetbrains.research.refactorinsight.data.*;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createLocationInfoFromJava;

public class ExtractClassJavaHandler extends JavaRefactoringHandler {


    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ExtractClassRefactoring ref = (ExtractClassRefactoring) refactoring;

        if (ref.getExtractedClass().isInterface()) {
            info.setGroup(Group.INTERFACE);
        } else if (ref.getExtractedClass().isAbstract()) {
            info.setGroup(Group.ABSTRACT);
        } else {
            info.setGroup(Group.CLASS);
        }

        info.setDetailsBefore(ref.getOriginalClass().getName())
                .setDetailsAfter(ref.getExtractedClass().getName())
                .setElementBefore(ref.getOriginalClass().getName())
                .setElementAfter(ref.getExtractedClass().getName())
                .setNameBefore(ref.getExtractedClass().getName())
                .setNameAfter(ref.getExtractedClass().getName());

        info.setFoldingDescriptorAfter(FoldingBuilder.fromClass(ref.getExtractedClass()));

        ref.getExtractedAttributes().keySet().forEach(attribute -> info.addMarking(
                createCodeRangeFromJava(attribute.codeRange()),
                createCodeRangeFromJava(ref.getExtractedClass().codeRange()),
                line -> line
                        .addOffset(
                                createLocationInfoFromJava(attribute.getLocationInfo()),
                                RefactoringLine.MarkingOption.REMOVE),
                RefactoringLine.MarkingOption.REMOVE,
                false));

        ref.getExtractedAttributes().values().forEach(attribute -> info.addMarking(
                createCodeRangeFromJava(ref.getOriginalClass().codeRange()),
                createCodeRangeFromJava(ref.getExtractedClass().codeRange()),
                line -> line
                        .addOffset(
                                createLocationInfoFromJava(attribute.getLocationInfo()),
                                RefactoringLine.MarkingOption.ADD),
                RefactoringLine.MarkingOption.ADD,
                false));

        ref.getExtractedOperations().keySet().forEach(operation -> info.addMarking(
                createCodeRangeFromJava(operation.codeRange()),
                createCodeRangeFromJava(ref.getExtractedClass().codeRange()),
                line -> line
                        .addOffset(
                                createLocationInfoFromJava(operation.getLocationInfo()),
                                RefactoringLine.MarkingOption.REMOVE),
                RefactoringLine.MarkingOption.REMOVE,
                false));

        ref.getExtractedOperations().values().forEach(operation -> info.addMarking(
                createCodeRangeFromJava(ref.getOriginalClass().codeRange()),
                createCodeRangeFromJava(ref.getExtractedClass().codeRange()),
                line -> line
                        .addOffset(
                                createLocationInfoFromJava(operation.getLocationInfo()),
                                RefactoringLine.MarkingOption.ADD),
                RefactoringLine.MarkingOption.ADD,
                false));
        return info;
    }

}
