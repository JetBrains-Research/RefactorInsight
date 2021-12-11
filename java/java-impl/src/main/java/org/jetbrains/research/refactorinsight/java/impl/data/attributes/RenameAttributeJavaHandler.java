package org.jetbrains.research.refactorinsight.java.impl.data.attributes;

import gr.uom.java.xmi.diff.RenameAttributeRefactoring;
import org.jetbrains.research.refactorinsight.common.data.Group;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.common.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.java.api.JavaRefactoringHandler;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.java.api.util.Utils.createCodeRangeFromJava;
import static org.jetbrains.research.refactorinsight.java.api.util.Utils.createLocationInfoFromJava;

public class RenameAttributeJavaHandler extends JavaRefactoringHandler {

    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        RenameAttributeRefactoring ref = (RenameAttributeRefactoring) refactoring;

        String classNameBefore = ref.getClassNameBefore();
        String classNameAfter = ref.getClassNameAfter();

        return info.setGroup(Group.ATTRIBUTE)
                .setGroupId(ref.getClassNameAfter() + "." + ref.getRenamedAttribute().getVariableName())
                .addMarking(createCodeRangeFromJava(ref.getOriginalAttribute().codeRange()),
                        createCodeRangeFromJava(ref.getRenamedAttribute().codeRange()),
                        line -> line.addOffset(createLocationInfoFromJava(ref.getOriginalAttribute().getLocationInfo()),
                                createLocationInfoFromJava(ref.getRenamedAttribute().getLocationInfo())),
                        RefactoringLine.MarkingOption.NONE, true)
                .setNameBefore(ref.getOriginalAttribute().getVariableDeclaration().toQualifiedString())
                .setNameAfter(ref.getRenamedAttribute().getVariableDeclaration().toQualifiedString())
                .setDetailsBefore(classNameBefore)
                .setDetailsAfter(classNameAfter);
    }

}
