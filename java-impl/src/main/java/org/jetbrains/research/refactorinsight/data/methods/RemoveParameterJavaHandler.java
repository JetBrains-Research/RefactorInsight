package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.diff.RemoveParameterRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.data.util.JavaUtils;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

public class RemoveParameterJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        RemoveParameterRefactoring ref = (RemoveParameterRefactoring) refactoring;

        String classNameBefore = ref.getOperationBefore().getClassName();
        String classNameAfter = ref.getOperationAfter().getClassName();

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setNameBefore(JavaUtils.calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(JavaUtils.calculateSignatureForJavaMethod(ref.getOperationAfter()))
                .setElementBefore(ref.getParameter().getVariableDeclaration().toQualifiedString())
                .setElementAfter(null)
                .addMarking(JavaUtils.createCodeRangeFromJava(ref.getOperationBefore().codeRange()),
                        JavaUtils.createCodeRangeFromJava(ref.getOperationAfter().codeRange()),
                        line -> line.addOffset(
                                JavaUtils.createLocationInfoFromJava(ref.getParameter().getVariableDeclaration().getLocationInfo()),
                                RefactoringLine.MarkingOption.REMOVE).setHasColumns(false),
                        RefactoringLine.MarkingOption.NONE, true);
    }

}
