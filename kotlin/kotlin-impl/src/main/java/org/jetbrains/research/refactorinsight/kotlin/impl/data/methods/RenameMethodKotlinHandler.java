package org.jetbrains.research.refactorinsight.kotlin.impl.data.methods;

import org.jetbrains.research.kotlinrminer.ide.Refactoring;
import org.jetbrains.research.kotlinrminer.ide.diff.refactoring.RenameOperationRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.KotlinRefactoringHandler;

import java.util.List;

import static org.jetbrains.research.refactorinsight.kotlin.impl.data.util.KotlinUtils.calculateSignatureForKotlinMethod;
import static org.jetbrains.research.refactorinsight.kotlin.impl.data.util.KotlinUtils.createCodeRangeFromKotlin;

public class RenameMethodKotlinHandler extends KotlinRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring,
                                   RefactoringInfo info) {
        RenameOperationRefactoring ref =
                (RenameOperationRefactoring) refactoring;
        String id = ref.getRenamedOperation().getClassName() + ".";

        if (ref.getRenamedOperation().isGetter()) {
            List<String> variables = ref.getRenamedOperation().getBody().getAllVariables();
            if (!variables.isEmpty()) {
                id += variables.get(0);
                info.setGroupId(id);
            }
        }

        String classNameBefore = ref.getOriginalOperation().getClassName();
        String classNameAfter = ref.getRenamedOperation().getClassName();

        info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setElementBefore(null)
                .setElementAfter(null)
                .setNameBefore(calculateSignatureForKotlinMethod(ref.getOriginalOperation()))
                .setNameAfter(calculateSignatureForKotlinMethod(ref.getRenamedOperation()));

        return info.addMarking(
                createCodeRangeFromKotlin(ref.getOriginalOperation().codeRange()),
                createCodeRangeFromKotlin(ref.getRenamedOperation().codeRange()),
                refactoringLine -> refactoringLine.setWord(new String[]{ref.getOriginalOperation().getName(),
                        null,
                        ref.getRenamedOperation().getName()
                }),
                RefactoringLine.MarkingOption.COLLAPSE,
                true);
    }

}
