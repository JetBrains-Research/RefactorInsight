package org.jetbrains.research.refactorinsight.data.types.methods;

import gr.uom.java.xmi.diff.RenameOperationRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

import java.util.List;

public class RenameMethodJavaHandler extends Handler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        RenameOperationRefactoring ref = (RenameOperationRefactoring) refactoring;

        String id = ref.getRenamedOperation().getClassName() + ".";
        if (ref.getRenamedOperation().isGetter()) {
            List<String> variables = ref.getRenamedOperation().getBody().getAllVariables();
            if (!variables.isEmpty()) {
                id += variables.get(0);
                info.setGroupId(id);
            }
        }

        if (ref.getRenamedOperation().isSetter()) {
            if (!ref.getRenamedOperation().getParameterNameList().isEmpty()) {
                id += ref.getRenamedOperation().getParameterNameList().get(0);
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
                .setNameBefore(StringUtils.calculateSignatureForJavaMethod(ref.getOriginalOperation()))
                .setNameAfter(StringUtils.calculateSignatureForJavaMethod(ref.getRenamedOperation()));

        return info.addMarking(CodeRange.createCodeRangeFromJava(ref.getOriginalOperation().codeRange()),
                CodeRange.createCodeRangeFromJava(ref.getRenamedOperation().codeRange()),
                refactoringLine -> refactoringLine.setWord(new String[]{ref.getOriginalOperation().getName(),
                        null,
                        ref.getRenamedOperation().getName()
                }),
                RefactoringLine.MarkingOption.COLLAPSE,
                true);
    }

}
