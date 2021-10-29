package org.jetbrains.research.refactorinsight.data.types.methods;

import org.jetbrains.research.kotlinrminer.api.Refactoring;
import org.jetbrains.research.kotlinrminer.diff.refactoring.RemoveParameterRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.adapters.LocationInfo;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;

public class RemoveParameterKotlinHandler extends Handler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring,
                                   RefactoringInfo info) {
        RemoveParameterRefactoring ref =
                (RemoveParameterRefactoring) refactoring;

        String classNameBefore = ref.getOperationBefore().getClassName();
        String classNameAfter = ref.getOperationAfter().getClassName();

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(StringUtils.calculateSignatureForKotlinMethod(ref.getOperationBefore()))
                .setNameAfter(StringUtils.calculateSignatureForKotlinMethod(ref.getOperationAfter()))
                .setElementBefore(ref.getParameter().getVariableDeclaration().toQualifiedString())
                .setElementAfter(null)
                .addMarking(CodeRange.createCodeRangeFromKotlin(ref.getOperationBefore().codeRange()),
                        CodeRange.createCodeRangeFromKotlin(ref.getOperationAfter().codeRange()),
                        line -> line.addOffset(
                                LocationInfo.createLocationInfoFromKotlin(ref.getParameter().getVariableDeclaration().getLocationInfo()),
                                RefactoringLine.MarkingOption.REMOVE).setHasColumns(false),
                        RefactoringLine.MarkingOption.NONE, true);
    }

}
