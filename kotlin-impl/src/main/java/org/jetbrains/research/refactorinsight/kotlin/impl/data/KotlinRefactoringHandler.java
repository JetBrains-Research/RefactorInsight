package org.jetbrains.research.refactorinsight.kotlin.impl.data;

import org.jetbrains.research.kotlinrminer.ide.Refactoring;
import org.jetbrains.research.refactorinsight.RefactoringProcessingException;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;

public abstract class KotlinRefactoringHandler {
    /**
     * Creates an {@link RefactoringInfo} instance from {@link Refactoring}.
     *
     * @param refactoring refactoring from kotlinRMiner.
     * @param projectPath
     * @return RefactoringInfo.
     */
    public final RefactoringInfo handle(Refactoring refactoring, String projectPath) throws RefactoringProcessingException {
        RefactoringInfo info = new RefactoringInfo().setProjectPath(projectPath);
        return specify(refactoring, info);
    }

    // Should be overridden in the specific Kotlin refactoring handler
    protected abstract RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) throws RefactoringProcessingException;
}
