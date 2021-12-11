package org.jetbrains.research.refactorinsight.kotlin.api;

import org.jetbrains.research.kotlinrminer.api.Refactoring;
import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;

public class KotlinRefactoringHandler {
    /**
     * Creates an {@link RefactoringInfo} instance from {@link Refactoring}.
     *
     * @param refactoring refactoring from kotlinRMiner.
     * @return RefactoringInfo.
     */
    public RefactoringInfo handle(Refactoring refactoring) {
        RefactoringInfo info = new RefactoringInfo();
        return specify(refactoring, info);
    }

    // Should be overrided in the specific Kotlin refactoring handler
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
        return null;
    }
}
