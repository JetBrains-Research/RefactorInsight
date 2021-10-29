package org.jetbrains.research.refactorinsight.data.types.attributes;

import gr.uom.java.xmi.diff.RenameAttributeRefactoring;
import org.jetbrains.research.refactorinsight.common.Handler;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.adapters.LocationInfo;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.refactoringminer.api.Refactoring;

public class RenameAttributeJavaHandler extends Handler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        RenameAttributeRefactoring ref = (RenameAttributeRefactoring) refactoring;

        String classNameBefore = ref.getClassNameBefore();
        String classNameAfter = ref.getClassNameAfter();

        return info.setGroup(Group.ATTRIBUTE)
                .setGroupId(ref.getClassNameAfter() + "." + ref.getRenamedAttribute().getVariableName())
                .addMarking(CodeRange.createCodeRangeFromJava(ref.getOriginalAttribute().codeRange()),
                        CodeRange.createCodeRangeFromJava(ref.getRenamedAttribute().codeRange()),
                        line -> line.addOffset(LocationInfo.createLocationInfoFromJava(ref.getOriginalAttribute().getLocationInfo()),
                                LocationInfo.createLocationInfoFromJava(ref.getRenamedAttribute().getLocationInfo())),
                        RefactoringLine.MarkingOption.NONE, true)
                .setNameBefore(ref.getOriginalAttribute().getVariableDeclaration().toQualifiedString())
                .setNameAfter(ref.getRenamedAttribute().getVariableDeclaration().toQualifiedString())
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter);
    }

}
