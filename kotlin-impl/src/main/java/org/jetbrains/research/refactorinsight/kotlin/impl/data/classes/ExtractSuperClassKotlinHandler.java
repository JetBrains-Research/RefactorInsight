package org.jetbrains.research.refactorinsight.kotlin.impl.data.classes;

import org.jetbrains.research.kotlinrminer.ide.Refactoring;
import org.jetbrains.research.kotlinrminer.ide.diff.refactoring.ExtractSuperClassRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.KotlinRefactoringHandler;

import static org.jetbrains.research.refactorinsight.kotlin.impl.data.util.KotlinUtils.createCodeRangeFromKotlin;

public class ExtractSuperClassKotlinHandler extends KotlinRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring,
                                   RefactoringInfo info) {
        ExtractSuperClassRefactoring ref = (ExtractSuperClassRefactoring) refactoring;

        if (ref.getExtractedClass().isInterface()) {
            info.setGroup(Group.INTERFACE);
        } else if (ref.getExtractedClass().isAbstract()) {
            info.setGroup(Group.ABSTRACT);
        } else {
            info.setGroup(Group.CLASS);
        }

        info.setDetailsBefore(ref.getExtractedClass().getPackageName())
                .setDetailsAfter(ref.getExtractedClass().getPackageName())
                .setNameBefore(ref.getExtractedClass().getName())
                .setNameAfter(ref.getExtractedClass().getName())
                .setMoreSided(true)
                .setRightPath(ref.getExtractedClass().codeRange().getFilePath());

        String[] nameSpace = ref.getExtractedClass().getName().split("\\.");
        String className = nameSpace[nameSpace.length - 1];

        ref.getUMLSubclassSet().forEach(subClass ->
                info.addMarking(createCodeRangeFromKotlin(subClass.codeRange(), info), null,
                        line -> line.setWord(new String[]{className, null, null}),
                        RefactoringLine.MarkingOption.COLLAPSE, true));

        return info;
    }

}
