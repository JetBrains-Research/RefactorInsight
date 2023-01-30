package org.jetbrains.research.refactorinsight.data;

import org.refactoringminer.api.Refactoring;

public class JavaRefactoringHandler {
    /**
     * Creates an {@link RefactoringInfo} instance from {@link Refactoring}.
     *
     * @param refactoring from RefactoringMiner.
     * @return RefactoringInfo.
     */
    public RefactoringInfo handle(Refactoring refactoring) {
        RefactoringInfo info = new RefactoringInfo();
        return specify(refactoring, info);
    }

    // Should be overridden in the specific Java refactoring handler
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        return null;
    }
}