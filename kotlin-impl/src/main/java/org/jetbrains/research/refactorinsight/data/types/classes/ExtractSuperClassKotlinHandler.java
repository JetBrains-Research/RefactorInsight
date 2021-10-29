package org.jetbrains.research.refactorinsight.data.types.classes;

import org.jetbrains.research.kotlinrminer.api.Refactoring;
import org.jetbrains.research.kotlinrminer.diff.refactoring.ExtractSuperClassRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;

public class ExtractSuperClassKotlinHandler extends Handler {

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
                info.addMarking(CodeRange.createCodeRangeFromKotlin(subClass.codeRange()), null,
                        line -> line.setWord(new String[]{className, null, null}),
                        RefactoringLine.MarkingOption.COLLAPSE, true));
        return info;
    }
}
