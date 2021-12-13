package org.jetbrains.research.refactorinsight.java.impl.data.attributes;

import gr.uom.java.xmi.diff.ExtractAttributeRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.java.impl.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.java.impl.data.util.Utils.createCodeRangeFromJava;

public class ExtractAttributeJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ExtractAttributeRefactoring ref = (ExtractAttributeRefactoring) refactoring;
        ref.leftSide().forEach(extraction ->
                info.addMarking(
                        createCodeRangeFromJava(extraction),
                        createCodeRangeFromJava(ref.getExtractedVariableDeclarationCodeRange()),
                        true
                ));

        return info.setGroup(Group.ATTRIBUTE)
                .setDetailsBefore(ref.getOriginalClass().getName())
                .setDetailsAfter(ref.getNextClass().getName())
                .setNameBefore(ref.getVariableDeclaration().getName() + " : " + ref.getVariableDeclaration().getType())
                .setNameAfter(ref.getVariableDeclaration().getName() + " : "
                        + ref.getVariableDeclaration().getType());
    }

}
