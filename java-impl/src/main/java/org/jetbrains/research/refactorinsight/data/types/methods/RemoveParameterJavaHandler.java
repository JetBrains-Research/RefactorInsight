package org.jetbrains.research.refactorinsight.data.types.methods;

import gr.uom.java.xmi.diff.RemoveParameterRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.adapters.LocationInfo;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

public class RemoveParameterJavaHandler extends Handler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        RemoveParameterRefactoring ref = (RemoveParameterRefactoring) refactoring;

        String classNameBefore = ref.getOperationBefore().getClassName();
        String classNameAfter = ref.getOperationAfter().getClassName();

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(StringUtils.calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(StringUtils.calculateSignatureForJavaMethod(ref.getOperationAfter()))
                .setElementBefore(ref.getParameter().getVariableDeclaration().toQualifiedString())
                .setElementAfter(null)
                .addMarking(CodeRange.createCodeRangeFromJava(ref.getOperationBefore().codeRange()),
                        CodeRange.createCodeRangeFromJava(ref.getOperationAfter().codeRange()),
                        line ->
                                line.addOffset(LocationInfo.createLocationInfoFromJava(ref.getParameter().getVariableDeclaration().getLocationInfo()),
                                        RefactoringLine.MarkingOption.REMOVE).setHasColumns(false),
                        RefactoringLine.MarkingOption.NONE, true);
    }

}
