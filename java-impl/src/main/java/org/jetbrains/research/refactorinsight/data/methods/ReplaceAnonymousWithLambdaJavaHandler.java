package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.diff.ReplaceAnonymousWithLambdaRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.util.JavaUtils;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;

public class ReplaceAnonymousWithLambdaJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ReplaceAnonymousWithLambdaRefactoring ref = (ReplaceAnonymousWithLambdaRefactoring) refactoring;

        String classNameBefore = ref.getOperationBefore().getClassName();
        String classNameAfter = ref.getOperationAfter().getClassName();

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(JavaUtils.calculateSignatureForVariableDeclarationContainer(ref.getOperationBefore()))
                .setNameAfter(JavaUtils.calculateSignatureForVariableDeclarationContainer(ref.getOperationAfter()))
                .addMarking(
                        createCodeRangeFromJava(ref.getAnonymousClass().codeRange()),
                        createCodeRangeFromJava(ref.getLambda().codeRange()),
                        true);
    }
    
}
