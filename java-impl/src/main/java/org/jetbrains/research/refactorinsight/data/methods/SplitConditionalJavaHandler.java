package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.decomposition.AbstractCodeFragment;
import gr.uom.java.xmi.diff.SplitConditionalRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.calculateSignatureForVariableDeclarationContainer;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;

public class SplitConditionalJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        SplitConditionalRefactoring ref = (SplitConditionalRefactoring) refactoring;

        ref.getSplitConditionals().forEach(conditional ->
                info.addMarking(createCodeRangeFromJava(ref.getOriginalConditional().codeRange()),
                        createCodeRangeFromJava(conditional.codeRange()), true));

        String conditionalString = ref.getOriginalConditional().getString();
        String oldConditional = (conditionalString.contains("\n") ? conditionalString.substring(0,
                conditionalString.indexOf("\n")) : conditionalString);

        StringBuilder splitConditionals = new StringBuilder();
        int i = 0;
        for(AbstractCodeFragment splitConditional : ref.getSplitConditionals()) {
            conditionalString = splitConditional.getString();
            String newConditional = (conditionalString.contains("\n") ? conditionalString.substring(0,
                    conditionalString.indexOf("\n")) : conditionalString);
            splitConditionals.append(newConditional);
            if(i < ref.getSplitConditionals().size()-1) {
                splitConditionals.append(", ");
            }
            i++;
        }

        //9c1d8e15

        return info.setGroup(Group.METHOD)
                .setNameBefore(calculateSignatureForVariableDeclarationContainer(ref.getOperationBefore()))
                .setNameAfter(calculateSignatureForVariableDeclarationContainer(ref.getOperationAfter()))
                .setElementBefore(oldConditional)
                .setElementAfter(splitConditionals.toString());
    }

}
