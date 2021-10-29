package org.jetbrains.research.refactorinsight.data.types.methods;

import org.jetbrains.research.kotlinrminer.api.Refactoring;
import org.jetbrains.research.kotlinrminer.diff.refactoring.RenameOperationRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;

import java.util.List;

public class RenameMethodKotlinHandler extends Handler {

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
                .setNameBefore(StringUtils.calculateSignatureForKotlinMethod(ref.getOriginalOperation()))
                .setNameAfter(StringUtils.calculateSignatureForKotlinMethod(ref.getRenamedOperation()));

        return info.addMarking(
                CodeRange.createCodeRangeFromKotlin(ref.getOriginalOperation().codeRange()),
                CodeRange.createCodeRangeFromKotlin(ref.getRenamedOperation().codeRange()),
                refactoringLine -> refactoringLine.setWord(new String[]{ref.getOriginalOperation().getName(),
                        null,
                        ref.getRenamedOperation().getName()
                }),
                RefactoringLine.MarkingOption.COLLAPSE,
                true);
    }

}
