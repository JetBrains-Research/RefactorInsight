package org.jetbrains.research.refactorinsight.data.types.methods;

import gr.uom.java.xmi.diff.RenameOperationRefactoring;

import java.util.List;

import org.jetbrains.research.refactorinsight.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.jetbrains.research.refactorinsight.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

public class RenameMethodHandler extends Handler {

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
        .setNameBefore(StringUtils.calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(StringUtils.calculateSignature(ref.getRenamedOperation()));

    return info.addMarking(
        new CodeRange(ref.getOriginalOperation().codeRange()),
        new CodeRange(ref.getRenamedOperation().codeRange()),
        refactoringLine -> refactoringLine.setWord(new String[]{
            ref.getOriginalOperation().getName(),
            null,
            ref.getRenamedOperation().getName()
        }),
        RefactoringLine.MarkingOption.COLLAPSE,
        true);
  }

  @Override
  public RefactoringInfo specify(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring,
                                 RefactoringInfo info) {
    org.jetbrains.research.kotlinrminer.diff.refactoring.RenameOperationRefactoring ref =
        (org.jetbrains.research.kotlinrminer.diff.refactoring.RenameOperationRefactoring) refactoring;

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
        .setNameBefore(StringUtils.calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(StringUtils.calculateSignature(ref.getRenamedOperation()));

    return info.addMarking(
        new CodeRange(ref.getOriginalOperation().codeRange()),
        new CodeRange(ref.getRenamedOperation().codeRange()),
        refactoringLine -> refactoringLine.setWord(new String[]{
            ref.getOriginalOperation().getName(),
            null,
            ref.getRenamedOperation().getName()
        }),
        RefactoringLine.MarkingOption.COLLAPSE,
        true);
  }
}
