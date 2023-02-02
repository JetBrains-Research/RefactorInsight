package org.jetbrains.research.refactorinsight.data.classes;

import gr.uom.java.xmi.diff.CollapseHierarchyRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

import static org.jetbrains.research.refactorinsight.data.util.JavaUtils.createCodeRangeFromJava;

public class CollapseHierarchyJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        CollapseHierarchyRefactoring ref = (CollapseHierarchyRefactoring) refactoring;
        return info.setGroup(Group.CLASS)
                .addMarking(createCodeRangeFromJava(ref.leftSide().get(0)),
                createCodeRangeFromJava(ref.rightSide().get(0)),
                true);
    }
    
}
