package org.jetbrains.research.refactorinsight.data.types.variables;

import gr.uom.java.xmi.diff.ExtractVariableRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

public class ExtractVariableJavaHandler extends Handler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        ExtractVariableRefactoring ref = (ExtractVariableRefactoring) refactoring;

        return info.setGroup(Group.VARIABLE)
                .setNameBefore(StringUtils.calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(StringUtils.calculateSignatureForJavaMethod(ref.getOperationAfter()))
                .setElementBefore(ref.getVariableDeclaration().getVariableDeclaration().toQualifiedString())
                .setElementAfter(null)
                .addMarking(CodeRange.createCodeRangeFromJava(ref.getOperationBefore().getBody().getCompositeStatement().codeRange()),
                        CodeRange.createCodeRangeFromJava(ref.getExtractedVariableDeclarationCodeRange()), true);
    }

}