package org.jetbrains.research.refactorinsight.java.impl.data.methods;

import gr.uom.java.xmi.diff.RemoveVariableAnnotationRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.java.impl.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.java.impl.data.util.Utils.*;

public class RemoveParameterAnnotationJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        RemoveVariableAnnotationRefactoring ref = (RemoveVariableAnnotationRefactoring) refactoring;
        String classNameBefore = ref.getOperationBefore().getClassName();
        String classNameAfter = ref.getOperationAfter().getClassName();

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(calculateSignatureForJavaMethod(ref.getOperationAfter()))
                .setElementAfter(null)
                .setElementBefore(ref.getAnnotation().toString() + " removed from "
                        + ref.getVariableAfter().getVariableDeclaration().getVariableName())
                .addMarking(createCodeRangeFromJava(ref.getOperationBefore().codeRange()),
                        createCodeRangeFromJava(ref.getOperationAfter().codeRange()),
                        line -> line.addOffset(
                                createLocationInfoFromJava(ref.getAnnotation().getLocationInfo()),
                                RefactoringLine.MarkingOption.REMOVE).setHasColumns(false),
                        RefactoringLine.MarkingOption.NONE,
                        true);
    }

}
