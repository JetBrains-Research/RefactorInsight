package org.jetbrains.research.refactorinsight.kotlin.impl.data.methods;

import org.jetbrains.research.kotlinrminer.api.Refactoring;
import org.jetbrains.research.kotlinrminer.diff.refactoring.AddParameterRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.KotlinRefactoringHandler;

import static org.jetbrains.research.refactorinsight.kotlin.impl.data.util.Utils.*;

public class AddParameterKotlinHandler extends KotlinRefactoringHandler {

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
                .setNameBefore(calculateSignatureForKotlinMethod(ref.getOperationBefore()))
                .setNameAfter(calculateSignatureForKotlinMethod(ref.getOperationAfter()))
                .setElementAfter(null)
                .setElementBefore(ref.getParameter().getVariableDeclaration().toQualifiedString())
                .addMarking(createCodeRangeFromKotlin(ref.getOperationBefore().codeRange()),
                        createCodeRangeFromKotlin(ref.getOperationAfter().codeRange()),
                        line -> line.addOffset(
                                        createLocationInfoFromKotlin(ref.getParameter().getVariableDeclaration().getLocationInfo()),
                                        RefactoringLine.MarkingOption.ADD)
                                .setHasColumns(false),
                        RefactoringLine.MarkingOption.NONE,
                        true);
    }

}
