package org.jetbrains.research.refactorinsight.data.types.methods;

import org.jetbrains.research.kotlinrminer.api.Refactoring;
import org.jetbrains.research.kotlinrminer.diff.refactoring.AddParameterRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.adapters.LocationInfo;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;

public class AddParameterKotlinHandler extends Handler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring,
                                   RefactoringInfo info) {
        org.jetbrains.research.kotlinrminer.diff.refactoring.AddParameterRefactoring ref =
                (AddParameterRefactoring) refactoring;

        String classNameBefore = ref.getOperationBefore().getClassName();
        String classNameAfter = ref.getOperationAfter().getClassName();

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(StringUtils.calculateSignatureForKotlinMethod(ref.getOperationBefore()))
                .setNameAfter(StringUtils.calculateSignatureForKotlinMethod(ref.getOperationAfter()))
                .setElementAfter(null)
                .setElementBefore(ref.getParameter().getVariableDeclaration().toQualifiedString())
                .addMarking(CodeRange.createCodeRangeFromKotlin(ref.getOperationBefore().codeRange()),
                        CodeRange.createCodeRangeFromKotlin(ref.getOperationAfter().codeRange()),
                        line -> line.addOffset(LocationInfo.createLocationInfoFromKotlin(
                                                ref.getParameter().getVariableDeclaration().getLocationInfo()),
                                        RefactoringLine.MarkingOption.ADD)
                                .setHasColumns(false),
                        RefactoringLine.MarkingOption.NONE,
                        true);
    }
}
