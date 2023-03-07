package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.diff.MergeConditionalRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.calculateSignatureForVariableDeclarationContainer;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.joinCodeFragments;

public class MergeConditionalJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        MergeConditionalRefactoring ref = (MergeConditionalRefactoring) refactoring;

        ref.getMergedConditionals().forEach(conditional ->
                info.addMarking(createCodeRangeFromJava(conditional.codeRange()),
                        createCodeRangeFromJava(ref.getNewConditional().codeRange()), true));

        String conditionalString = ref.getNewConditional().getString();
        String newConditional = conditionalString.contains("\n") ? conditionalString.substring(0,
                conditionalString.indexOf("\n")) : conditionalString;

        String oldConditionals = joinCodeFragments(ref.getMergedConditionals());

        return info.setGroup(Group.METHOD)
                .setNameBefore(calculateSignatureForVariableDeclarationContainer(ref.getOperationBefore()))
                .setNameAfter(calculateSignatureForVariableDeclarationContainer(ref.getOperationAfter()))
                .setElementBefore(oldConditionals)
                .setElementAfter(newConditional);
    }

}
