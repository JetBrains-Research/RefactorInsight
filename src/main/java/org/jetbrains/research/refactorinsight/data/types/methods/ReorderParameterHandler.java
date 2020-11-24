package org.jetbrains.research.refactorinsight.data.types.methods;

import com.intellij.openapi.util.Pair;
import gr.uom.java.xmi.decomposition.VariableDeclaration;
import gr.uom.java.xmi.diff.ReorderParameterRefactoring;

import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.research.refactorinsight.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.jetbrains.research.refactorinsight.utils.StringUtils;
import org.refactoringminer.api.Refactoring;

public class ReorderParameterHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ReorderParameterRefactoring ref = (ReorderParameterRefactoring) refactoring;

    String classNameBefore = ref.getOperationBefore().getClassName();
    String classNameAfter = ref.getOperationAfter().getClassName();
    List<VariableDeclaration> befores = ref.getParametersBefore();
    List<VariableDeclaration> afters = ref.getParametersAfter();
    IntStream.range(0, Math.min(befores.size(), afters.size()))
        .mapToObj(i -> new Pair<>(befores.get(i), afters.get(i)))
        .forEach(x -> {
          if (!x.first.getVariableDeclaration().getType()
              .equals(x.second.getVariableDeclaration().getType())
              || !x.first.getVariableName().equals(x.second.getVariableName())) {
            info.addMarking(new CodeRange(x.first.codeRange()), new CodeRange(x.second.codeRange()), true);
          }
        });
    return info.setGroup(Group.METHOD)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setNameBefore(StringUtils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(StringUtils.calculateSignature(ref.getOperationAfter()));
  }

  @Override
  public RefactoringInfo specify(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring,
                                 RefactoringInfo info) {
    org.jetbrains.research.kotlinrminer.diff.refactoring.ReorderParameterRefactoring ref =
        (org.jetbrains.research.kotlinrminer.diff.refactoring.ReorderParameterRefactoring) refactoring;

    String classNameBefore = ref.getOperationBefore().getClassName();
    String classNameAfter = ref.getOperationAfter().getClassName();
    List<org.jetbrains.research.kotlinrminer.decomposition.VariableDeclaration> befores = ref.getParametersBefore();
    List<org.jetbrains.research.kotlinrminer.decomposition.VariableDeclaration> afters = ref.getParametersAfter();
    IntStream.range(0, Math.min(befores.size(), afters.size()))
        .mapToObj(i -> new Pair<>(befores.get(i), afters.get(i)))
        .forEach(x -> {
          if (!x.first.getVariableDeclaration().getType()
              .equals(x.second.getVariableDeclaration().getType())
              || !x.first.getVariableName().equals(x.second.getVariableName())) {
            info.addMarking(new CodeRange(x.first.codeRange()), new CodeRange(x.second.codeRange()), true);
          }
        });
    return info.setGroup(Group.METHOD)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setNameBefore(StringUtils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(StringUtils.calculateSignature(ref.getOperationAfter()));
  }
}
