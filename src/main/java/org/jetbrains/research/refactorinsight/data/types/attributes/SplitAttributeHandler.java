package org.jetbrains.research.refactorinsight.data.types.attributes;

import gr.uom.java.xmi.decomposition.VariableDeclaration;
import gr.uom.java.xmi.diff.SplitAttributeRefactoring;
import java.util.stream.Collectors;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.refactoringminer.api.Refactoring;

public class SplitAttributeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    SplitAttributeRefactoring ref = (SplitAttributeRefactoring) refactoring;
    ref.getSplitAttributes().forEach(attr ->
        info.addMarking(ref.getOldAttribute().codeRange(), attr.codeRange(), true));

    String classNameBefore = ref.getClassNameBefore();
    String classNameAfter = ref.getClassNameAfter();

    return info.setGroup(Group.ATTRIBUTE)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setNameBefore(ref.getOldAttribute().getVariableName())
        .setNameAfter(ref.getSplitAttributes().stream().map(VariableDeclaration::getVariableName)
            .collect(Collectors.joining()));
  }
}
