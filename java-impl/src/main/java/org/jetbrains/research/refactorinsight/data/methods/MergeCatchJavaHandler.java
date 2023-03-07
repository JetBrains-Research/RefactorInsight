package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.diff.MergeCatchRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.calculateSignatureForVariableDeclarationContainer;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.joinCodeFragments;

public class MergeCatchJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        MergeCatchRefactoring ref = (MergeCatchRefactoring) refactoring;

        ref.getMergedCatchBlocks().forEach(catchBlock ->
                info.addMarking(createCodeRangeFromJava(catchBlock.codeRange()),
                        createCodeRangeFromJava(ref.getNewCatchBlock().codeRange()), true));

        String catchBlockString = ref.getNewCatchBlock().getString();
        String newCatchBlock = catchBlockString.contains("\n") ? catchBlockString.substring(0,
                catchBlockString.indexOf("\n")) : catchBlockString;

        String oldCatchBlocks = joinCodeFragments(ref.getMergedCatchBlocks());

        return info.setGroup(Group.METHOD)
                .setNameBefore(calculateSignatureForVariableDeclarationContainer(ref.getOperationBefore()))
                .setNameAfter(calculateSignatureForVariableDeclarationContainer(ref.getOperationAfter()))
                .setElementBefore(oldCatchBlocks)
                .setElementAfter(newCatchBlock);
    }

}
