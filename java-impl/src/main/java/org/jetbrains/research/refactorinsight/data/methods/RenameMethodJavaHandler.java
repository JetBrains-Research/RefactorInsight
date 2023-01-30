package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.diff.RenameOperationRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.data.util.JavaUtils;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import java.util.List;

public class RenameMethodJavaHandler extends JavaRefactoringHandler {

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
                .setNameBefore(JavaUtils.calculateSignatureForJavaMethod(ref.getOriginalOperation()))
                .setNameAfter(JavaUtils.calculateSignatureForJavaMethod(ref.getRenamedOperation()));

        return info.addMarking(JavaUtils.createCodeRangeFromJava(ref.getOriginalOperation().codeRange()),
                JavaUtils.createCodeRangeFromJava(ref.getRenamedOperation().codeRange()),
                refactoringLine -> refactoringLine.setWord(new String[]{ref.getOriginalOperation().getName(),
                        null,
                        ref.getRenamedOperation().getName()
                }),
                RefactoringLine.MarkingOption.COLLAPSE,
                true);
    }

}
