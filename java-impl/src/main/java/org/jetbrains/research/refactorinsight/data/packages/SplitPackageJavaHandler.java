package org.jetbrains.research.refactorinsight.data.packages;

import gr.uom.java.xmi.diff.SplitPackageRefactoring;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.refactoringminer.api.Refactoring;

public class SplitPackageJavaHandler extends JavaRefactoringHandler {
    @Override
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        SplitPackageRefactoring ref = (SplitPackageRefactoring) refactoring;

        return info.setGroup(Group.PACKAGE)
                .setNameBefore(ref.getOriginalPackage())
                .setNameAfter(String.join("", ref.getSplitPackages()));
    }

}
