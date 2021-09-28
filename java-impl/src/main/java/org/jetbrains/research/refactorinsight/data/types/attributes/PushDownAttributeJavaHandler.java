package org.jetbrains.research.refactorinsight.data.types.attributes;

import gr.uom.java.xmi.diff.PushDownAttributeRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

public class PushDownAttributeJavaHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    PushDownAttributeRefactoring ref = (PushDownAttributeRefactoring) refactoring;

    String classNameBefore = ref.getSourceClassName();
    String classNameAfter = ref.getTargetClassName();

    return info.setGroup(Group.ATTRIBUTE)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setNameBefore(ref.getOriginalAttribute().getVariableDeclaration().toQualifiedString())
        .setNameAfter(ref.getMovedAttribute().getVariableDeclaration().toQualifiedString())
        .addMarking(new CodeRange(ref.getSourceAttributeCodeRangeBeforeMove()),
            new CodeRange(ref.getTargetAttributeCodeRangeAfterMove()), true);
  }

}
