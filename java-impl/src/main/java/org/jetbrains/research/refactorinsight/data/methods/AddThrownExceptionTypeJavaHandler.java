package org.jetbrains.research.refactorinsight.data.methods;

import gr.uom.java.xmi.diff.AddThrownExceptionTypeRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.util.JavaUtils;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.RefactoringLine.MarkingOption.ADD;
import static org.jetbrains.research.refactorinsight.data.RefactoringLine.MarkingOption.NONE;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;
import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createLocationInfoFromJava;

public class AddThrownExceptionTypeJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        AddThrownExceptionTypeRefactoring ref = (AddThrownExceptionTypeRefactoring) refactoring;

        String classNameBefore = ref.getOperationBefore().getClassName();
        String classNameAfter = ref.getOperationAfter().getClassName();

        return info.setGroup(Group.METHOD)
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter)
                .setElementBefore(ref.getExceptionType().toString())
                .setElementAfter(null)
                .addMarking(
                        createCodeRangeFromJava(ref.getOperationBefore().codeRange()),
                        createCodeRangeFromJava(ref.getExceptionType().codeRange()),
                        line -> line.addOffset(createLocationInfoFromJava(ref.getExceptionType().getLocationInfo()), ADD)
                                .setHasColumns(false),
                        NONE, true)
                .setNameBefore(JavaUtils.calculateSignatureForJavaMethod(ref.getOperationBefore()))
                .setNameAfter(JavaUtils.calculateSignatureForJavaMethod(ref.getOperationAfter()));
    }
    
}
