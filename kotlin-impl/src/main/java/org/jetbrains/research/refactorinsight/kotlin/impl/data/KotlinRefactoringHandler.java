package org.jetbrains.research.refactorinsight.kotlin.impl.data;

import org.jetbrains.research.kotlinrminer.ide.Refactoring;
import org.jetbrains.research.refactorinsight.RefactoringProcessingException;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;

public class KotlinRefactoringHandler {
    /**
     * Creates an {@link RefactoringInfo} instance from {@link Refactoring}.
     *
     * @param refactoring refactoring from kotlinRMiner.
     * @return RefactoringInfo.
     */
    public RefactoringInfo handle(Refactoring refactoring) throws RefactoringProcessingException {
        RefactoringInfo info = new RefactoringInfo();
        return specify(refactoring, info);
    }

    // Should be overridden in the specific Kotlin refactoring handler
    public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) throws RefactoringProcessingException {
        return null;
    }
}
