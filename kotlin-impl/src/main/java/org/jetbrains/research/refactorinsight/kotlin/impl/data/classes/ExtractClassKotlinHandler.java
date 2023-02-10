package org.jetbrains.research.refactorinsight.kotlin.impl.data.classes;

import org.jetbrains.research.kotlinrminer.ide.Refactoring;
import org.jetbrains.research.kotlinrminer.ide.diff.refactoring.ExtractClassRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.diff.VisualizationType;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.KotlinRefactoringHandler;

import static org.jetbrains.research.refactorinsight.kotlin.impl.data.util.KotlinUtils.createCodeRangeFromKotlin;

public class ExtractClassKotlinHandler extends KotlinRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring,
                                   RefactoringInfo info) {
        ExtractClassRefactoring ref = (ExtractClassRefactoring) refactoring;

        if (ref.getExtractedClass().isInterface()) {
            info.setGroup(Group.INTERFACE);
        } else if (ref.getExtractedClass().isAbstract()) {
            info.setGroup(Group.ABSTRACT);
        } else {
            info.setGroup(Group.CLASS);
        }

        info.setDetailsBefore(ref.getOriginalClass().getPackageName())
                .setDetailsAfter(ref.getExtractedClass().getPackageName())
                .setElementBefore(ref.getOriginalClass().getName())
                .setElementAfter(ref.getExtractedClass().getName())
                .setNameBefore(ref.getExtractedClass().getName())
                .setNameAfter(ref.getExtractedClass().getName());

        if (ref.getAttributeOfExtractedClassTypeInOriginalClass() != null) {
            info.setThreeSided(true);
            ref.getExtractedOperations().forEach(operation -> info.addMarking(
                    createCodeRangeFromKotlin(operation.codeRange(), info),
                    createCodeRangeFromKotlin(ref.getExtractedClass().codeRange(), info),
                    createCodeRangeFromKotlin(ref.getAttributeOfExtractedClassTypeInOriginalClass().codeRange(), info),
                    VisualizationType.LEFT,
                    null,
                    RefactoringLine.MarkingOption.NONE,
                    true));

            ref.getExtractedAttributes().forEach(operation -> info.addMarking(
                    createCodeRangeFromKotlin(operation.codeRange(), info),
                    createCodeRangeFromKotlin(ref.getExtractedClass().codeRange(), info),
                    createCodeRangeFromKotlin(ref.getAttributeOfExtractedClassTypeInOriginalClass().codeRange(), info),
                    VisualizationType.LEFT,
                    null,
                    RefactoringLine.MarkingOption.NONE,
                    true));


            String[] nameSpace = ref.getExtractedClass().getName().split("\\.");
            String className = nameSpace[nameSpace.length - 1];

            info.addMarking(createCodeRangeFromKotlin(ref.getOriginalClass().codeRange(), info),
                    createCodeRangeFromKotlin(ref.getExtractedClass().codeRange(), info),
                    createCodeRangeFromKotlin(ref.getAttributeOfExtractedClassTypeInOriginalClass().codeRange(), info),
                    VisualizationType.RIGHT,
                    refactoringLine -> refactoringLine.setWord(new String[]{
                            null,
                            className,
                            null
                    }),
                    RefactoringLine.MarkingOption.EXTRACT,
                    true);
        } else {
            ref.getExtractedOperations().forEach(operation -> info.addMarking(
                    createCodeRangeFromKotlin(operation.codeRange(), info),
                    createCodeRangeFromKotlin(ref.getExtractedClass().codeRange(), info), true));

            ref.getExtractedAttributes().forEach(operation -> info.addMarking(
                    createCodeRangeFromKotlin(operation.codeRange(), info),
                    createCodeRangeFromKotlin(ref.getExtractedClass().codeRange(), info), true));
        }

        return info;
    }
}
