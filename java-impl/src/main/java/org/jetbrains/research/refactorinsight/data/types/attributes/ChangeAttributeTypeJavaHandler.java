package org.jetbrains.research.refactorinsight.data.types.attributes;

import gr.uom.java.xmi.diff.ChangeAttributeTypeRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

public class ChangeAttributeTypeJavaHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ChangeAttributeTypeRefactoring ref = (ChangeAttributeTypeRefactoring) refactoring;

    String classNameBefore = ref.getClassNameBefore();
    String classNameAfter = ref.getClassNameAfter();

    return info.setGroup(Group.ATTRIBUTE)
        .setGroupId(ref.getClassNameAfter() + "." + ref.getChangedTypeAttribute().getVariableDeclaration().getVariableName())
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setNameBefore(ref.getOriginalAttribute().getVariableDeclaration().toQualifiedString())
        .setNameAfter(ref.getChangedTypeAttribute().getVariableDeclaration().toQualifiedString())
        .addMarking(new CodeRange(ref.getOriginalAttribute().getType().codeRange()),
            new CodeRange(ref.getChangedTypeAttribute().getType().codeRange()),
            true);
  }

}
