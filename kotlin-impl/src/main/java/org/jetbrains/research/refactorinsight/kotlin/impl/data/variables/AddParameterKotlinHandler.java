package org.jetbrains.research.refactorinsight.kotlin.impl.data.variables;

import org.jetbrains.research.kotlinrminer.ide.Refactoring;
import org.jetbrains.research.kotlinrminer.ide.diff.refactoring.AddParameterRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.KotlinRefactoringHandler;

import static org.jetbrains.research.refactorinsight.kotlin.impl.data.util.KotlinUtils.*;

public class AddParameterKotlinHandler extends KotlinRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring,
                                   RefactoringInfo info) {
        AddParameterRefactoring ref =
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
                .addMarking(createCodeRangeFromKotlin(ref.getOperationBefore().codeRange(), info),
                        createCodeRangeFromKotlin(ref.getOperationAfter().codeRange(), info),
                        line -> line.addOffset(
                                        createLocationInfoFromKotlin(ref.getParameter().getVariableDeclaration().getLocationInfo()),
                                        RefactoringLine.MarkingOption.ADD)
                                .setHasColumns(false),
                        RefactoringLine.MarkingOption.NONE,
                        true);
    }

}
