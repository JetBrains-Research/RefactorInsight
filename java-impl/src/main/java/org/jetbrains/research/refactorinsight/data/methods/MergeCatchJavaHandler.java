package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.decomposition.AbstractCodeFragment;
import gr.uom.java.xmi.diff.MergeCatchRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.calculateSignatureForVariableDeclarationContainer;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;

public class MergeCatchJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        MergeCatchRefactoring ref = (MergeCatchRefactoring) refactoring;

        ref.getMergedCatchBlocks().forEach(catchBlock ->
                info.addMarking(createCodeRangeFromJava(catchBlock.codeRange()),
                        createCodeRangeFromJava(ref.getNewCatchBlock().codeRange()), true));

        String catchBlockString = ref.getNewCatchBlock().getString();
        String newCatchBlock = (catchBlockString.contains("\n") ? catchBlockString.substring(0,
                catchBlockString.indexOf("\n")) : catchBlockString);

        StringBuilder oldCatchBlocks = new StringBuilder();
        int i = 0;
        for(AbstractCodeFragment mergedCatchBlock : ref.getMergedCatchBlocks()) {
            catchBlockString = mergedCatchBlock.getString();
            String oldConditional = (catchBlockString.contains("\n") ? catchBlockString.substring(0,
                    catchBlockString.indexOf("\n")) : catchBlockString);
            oldCatchBlocks.append(oldConditional);
            if(i < ref.getMergedCatchBlocks().size()-1) {
                oldCatchBlocks.append(", ");
            }
            i++;
        }

        return info.setGroup(Group.METHOD)
                .setNameBefore(calculateSignatureForVariableDeclarationContainer(ref.getOperationBefore()))
                .setNameAfter(calculateSignatureForVariableDeclarationContainer(ref.getOperationAfter()))
                .setElementBefore(oldCatchBlocks.toString())
                .setElementAfter(newCatchBlock);
    }

}
