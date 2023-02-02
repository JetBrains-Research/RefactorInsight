package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.diff.ReplacePipelineWithLoopRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.util.JavaUtils;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;

public class ReplacePipelineWithLoopJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ReplacePipelineWithLoopRefactoring ref = (ReplacePipelineWithLoopRefactoring) refactoring;

        String classNameBefore = ref.getOperationBefore().getClassName();
        String classNameAfter = ref.getOperationAfter().getClassName();

        ref.getCodeFragmentsBefore().forEach(fragmentBefore ->
                ref.getCodeFragmentsAfter().forEach(fragmentAfter ->
                        info.addMarking(createCodeRangeFromJava(fragmentBefore.codeRange()),
                                createCodeRangeFromJava(fragmentAfter.codeRange()),
                                true)));

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(JavaUtils.calculateSignatureForVariableDeclarationContainer(ref.getOperationBefore()))
                .setNameAfter(JavaUtils.calculateSignatureForVariableDeclarationContainer(ref.getOperationAfter()));
    }
    
}
