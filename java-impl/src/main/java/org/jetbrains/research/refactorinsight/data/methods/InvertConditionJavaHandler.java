package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.diff.InvertConditionRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.util.JavaUtils;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.calculateSignatureForVariableDeclarationContainer;

public class InvertConditionJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        InvertConditionRefactoring ref = (InvertConditionRefactoring) refactoring;

        String conditionalString = ref.getOriginalConditional().getString();
        String oldConditional = (conditionalString.contains("\n") ? conditionalString.substring(0,
                conditionalString.indexOf("\n")) : conditionalString);

        String invertConditionalString = ref.getInvertedConditional().getString();
        String invertedConditional = (invertConditionalString.contains("\n") ? invertConditionalString.substring(0,
                invertConditionalString.indexOf("\n")) : invertConditionalString);

        return info.setGroup(Group.METHOD)
                .setNameBefore(calculateSignatureForVariableDeclarationContainer(ref.getOperationBefore()))
                .setNameAfter(calculateSignatureForVariableDeclarationContainer(ref.getOperationAfter()))
                .setElementBefore(oldConditional)
                .setElementAfter(invertedConditional)
                .addMarking(JavaUtils.createCodeRangeFromJava(ref.getOriginalConditional().codeRange()),
                        JavaUtils.createCodeRangeFromJava(ref.getInvertedConditional().codeRange()),
                        true);
    }

}
