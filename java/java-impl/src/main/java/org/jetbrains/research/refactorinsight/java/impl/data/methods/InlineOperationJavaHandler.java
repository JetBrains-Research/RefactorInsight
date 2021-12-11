package org.jetbrains.research.refactorinsight.java.impl.data.methods;

import gr.uom.java.xmi.diff.InlineOperationRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.java.api.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.java.api.util.Utils.calculateSignatureForJavaMethod;
import static org.jetbrains.research.refactorinsight.java.api.util.Utils.createCodeRangeFromJava;

public class InlineOperationJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        InlineOperationRefactoring ref = (InlineOperationRefactoring) refactoring;

        ref.getInlinedOperationInvocations().forEach(c ->
                info.addMarking(createCodeRangeFromJava(c.codeRange()),
                        createCodeRangeFromJava(ref.getInlinedCodeRangeInTargetOperation()),
                        true));
        String classNameBefore = ref.getTargetOperationBeforeInline().getClassName();
        String classNameAfter = ref.getTargetOperationAfterInline().getClassName();

        info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setElementBefore(ref.getInlinedOperation().getName())
                .setElementAfter(null)
                .setNameBefore(calculateSignatureForJavaMethod(ref.getTargetOperationBeforeInline()))
                .setNameAfter(calculateSignatureForJavaMethod(ref.getTargetOperationAfterInline()))
                .addMarking(createCodeRangeFromJava(ref.getTargetOperationCodeRangeBeforeInline()),
                        createCodeRangeFromJava(ref.getTargetOperationCodeRangeAfterInline()),
                        false);

        if (ref.getInlinedOperation().codeRange().getFilePath()
                .equals(ref.getTargetOperationAfterInline().codeRange().getFilePath())) {
            info.addMarking(createCodeRangeFromJava(ref.getInlinedOperationCodeRange()),
                    createCodeRangeFromJava(ref.getInlinedCodeRangeInTargetOperation()),
                    null,
                    RefactoringLine.MarkingOption.REMOVE,
                    false);
        }
        return info;
    }

}
