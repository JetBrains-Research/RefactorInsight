package org.jetbrains.research.refactorinsight.data;

import org.refactoringminer.api.Refactoring;

public abstract class JavaRefactoringHandler {
    /**
     * Creates an {@link RefactoringInfo} instance from {@link Refactoring}.
     *
     * @param refactoring from RefactoringMiner.
     * @param projectPath
     * @return RefactoringInfo.
     */
    public final RefactoringInfo handle(Refactoring refactoring, String projectPath) {
        RefactoringInfo info = new RefactoringInfo().setProjectPath(projectPath);
        return specify(refactoring, info);
    }

    // Should be overridden in the specific Java refactoring handler
    protected abstract RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info);
}
