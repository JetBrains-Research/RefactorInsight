package org.jetbrains.research.refactorinsight.data.classes;

import gr.uom.java.xmi.diff.ExtractSuperclassRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;

public class ExtractSuperClassJavaHandler extends JavaRefactoringHandler {

    @Override

    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ExtractSuperclassRefactoring ref = (ExtractSuperclassRefactoring) refactoring;

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

        ref.getUMLSubclassSetBefore().forEach(subClass ->
                info.addMarking(createCodeRangeFromJava(subClass.codeRange()), null, line ->
                        line.setWord(new String[]{className, null, null}), RefactoringLine.MarkingOption.COLLAPSE, true));

        return info;
    }

}
