package org.jetbrains.research.refactorinsight.data.types.attributes;

import gr.uom.java.xmi.decomposition.VariableDeclaration;
import gr.uom.java.xmi.diff.MergeAttributeRefactoring;
import java.util.stream.Collectors;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.refactoringminer.api.Refactoring;

public class MergeAttributeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    MergeAttributeRefactoring ref = (MergeAttributeRefactoring) refactoring;

    String classNameAfter = ref.getClassNameAfter();
    String classNameBefore = ref.getClassNameBefore();

    ref.getMergedAttributes().forEach(attr ->
        info.addMarking(attr.codeRange(), ref.getNewAttribute().codeRange(), true));

    return info.setGroup(Group.ATTRIBUTE)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setNameBefore(ref.getMergedAttributes().stream()
            .map(VariableDeclaration::getVariableName)
            .collect(Collectors.joining()))
        .setNameAfter(ref.getNewAttribute().getVariableDeclaration().getVariableName());

  }
}
