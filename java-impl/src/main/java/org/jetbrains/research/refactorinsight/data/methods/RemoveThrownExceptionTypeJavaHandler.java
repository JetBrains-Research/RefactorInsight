package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.diff.RemoveThrownExceptionTypeRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.util.JavaUtils;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.RefactoringLine.MarkingOption.NONE;
import static org.jetbrains.research.refactorinsight.data.RefactoringLine.MarkingOption.REMOVE;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createLocationInfoFromJava;

public class RemoveThrownExceptionTypeJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        RemoveThrownExceptionTypeRefactoring ref = (RemoveThrownExceptionTypeRefactoring) refactoring;

        String classNameBefore = ref.getOperationBefore().getClassName();
        String classNameAfter = ref.getOperationAfter().getClassName();

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setElementBefore(ref.getExceptionType().toString())
                .setElementAfter(null)
                .addMarking(
                        createCodeRangeFromJava(ref.getExceptionType().codeRange()),
                        createCodeRangeFromJava(ref.getOperationAfter().codeRange()),
                        line -> line.addOffset(createLocationInfoFromJava(ref.getExceptionType().getLocationInfo()), REMOVE)
                                .setHasColumns(false),
                        NONE, true)
                .setNameBefore(JavaUtils.calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(JavaUtils.calculateSignatureForJavaMethod(ref.getOperationAfter()));
    }
    
}
