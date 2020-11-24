package org.jetbrains.research.refactorinsight.data.types.methods;

import gr.uom.java.xmi.diff.AddParameterRefactoring;
import org.jetbrains.research.refactorinsight.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.adapters.LocationInfo;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine.MarkingOption;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.jetbrains.research.refactorinsight.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

public class AddParameterHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    AddParameterRefactoring ref = (AddParameterRefactoring) refactoring;

    String classNameBefore = ref.getOperationBefore().getClassName();
    String classNameAfter = ref.getOperationAfter().getClassName();

    return info.setGroup(Group.METHOD)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setNameBefore(StringUtils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(StringUtils.calculateSignature(ref.getOperationAfter()))
        .setElementAfter(null)
        .setElementBefore(ref.getParameter().getVariableDeclaration().toQualifiedString())
        .addMarking(new CodeRange(ref.getOperationBefore().codeRange()),
            new CodeRange(ref.getOperationAfter().codeRange()),
            line -> line.addOffset(
                new LocationInfo(ref.getParameter().getVariableDeclaration().getLocationInfo()), MarkingOption.ADD)
                .setHasColumns(false),
            MarkingOption.NONE,
            true);
  }

  @Override
  public RefactoringInfo specify(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring,
                                 RefactoringInfo info) {
    org.jetbrains.research.kotlinrminer.diff.refactoring.AddParameterRefactoring ref =
        (org.jetbrains.research.kotlinrminer.diff.refactoring.AddParameterRefactoring) refactoring;

    String classNameBefore = ref.getOperationBefore().getClassName();
    String classNameAfter = ref.getOperationAfter().getClassName();

    return info.setGroup(Group.METHOD)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setNameBefore(StringUtils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(StringUtils.calculateSignature(ref.getOperationAfter()))
        .setElementAfter(null)
        .setElementBefore(ref.getParameter().getVariableDeclaration().toQualifiedString())
        .addMarking(new CodeRange(ref.getOperationBefore().codeRange()),
            new CodeRange(ref.getOperationAfter().codeRange()),
            line -> line.addOffset(new LocationInfo(ref.getParameter().getVariableDeclaration().getLocationInfo()),
                MarkingOption.ADD)
                .setHasColumns(false),
            MarkingOption.NONE,
            true);
  }
}
