package org.jetbrains.research.refactorinsight.data.types.attributes;

import gr.uom.java.xmi.diff.RenameAttributeRefactoring;
import org.jetbrains.research.refactorinsight.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.adapters.LocationInfo;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.refactoringminer.api.Refactoring;

public class RenameAttributeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RenameAttributeRefactoring ref = (RenameAttributeRefactoring) refactoring;

    String classNameBefore = ref.getClassNameBefore();
    String classNameAfter = ref.getClassNameAfter();

    return info.setGroup(Group.ATTRIBUTE)
        .setGroupId(ref.getClassNameAfter() + "." + ref.getRenamedAttribute().getName())
        .addMarking(new CodeRange(ref.getOriginalAttribute().codeRange()),
            new CodeRange(ref.getRenamedAttribute().codeRange()),
            line -> line.addOffset(new LocationInfo(ref.getOriginalAttribute().getLocationInfo()),

                new LocationInfo(ref.getRenamedAttribute().getLocationInfo())),
            RefactoringLine.MarkingOption.NONE, true)
        .setNameBefore(ref.getOriginalAttribute().getVariableDeclaration().toQualifiedString())
        .setNameAfter(ref.getRenamedAttribute().getVariableDeclaration().toQualifiedString())
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter);

  }


  @Override
  public RefactoringInfo specify(org.jetbrains.research.kotlinrminer.api.Refactoring refactoring,
                                 RefactoringInfo info) {
    //This kind of refactoring is not supported by kotlinRMiner yet.
    return null;
  }
}
