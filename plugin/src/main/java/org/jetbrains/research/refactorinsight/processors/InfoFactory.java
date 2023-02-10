package org.jetbrains.research.refactorinsight.processors;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.research.refactorinsight.RefactoringProcessingException;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.KotlinRefactoringHandler;
import org.refactoringminer.api.Refactoring;

/**
 * Creates {@link RefactoringInfo} objects for refactorings provided by RefactoringMiner and kotlinRMiner.
 */
public class InfoFactory {
    @Nullable
    public RefactoringInfo create(Object refactoring, String projectPath) {
        if (refactoring instanceof Refactoring javaRefactoring) {
            return create(javaRefactoring, projectPath);
        } else if (refactoring instanceof org.jetbrains.research.kotlinrminer.ide.Refactoring kotlinRefactoring) {
            return create(kotlinRefactoring, projectPath);
        } else if (refactoring == null) {
            throw new NullPointerException("refactoring is null");
        } else {
            throw new IllegalArgumentException("Unknown refactoring type: " + refactoring.getClass().getName());
        }
    }

    /**
     * Creates a relevant {@link RefactoringInfo} instance for a given Refactoring provided by RefactoringMiner.
     *
     * @param refactoring to be analyzed.
     * @param projectPath
     * @return resulting RefactoringInfo.
     */
    @Nullable
    public RefactoringInfo create(Refactoring refactoring, String projectPath) {
        final JavaRefactoringHandler handler = RefactoringType.valueOf(refactoring.getRefactoringType().name()).getJavaHandler();
        return handler == null ? null : handler.handle(refactoring, projectPath)
                .setProjectPath(projectPath)
                .setType(refactoring.getRefactoringType().getDisplayName());
    }

    /**
     * Creates a relevant {@link RefactoringInfo} instance for a given Refactoring provided by kotlinRMiner.
     *
     * @param refactoring to be analyzed.
     * @param projectPath
     * @return resulting RefactoringInfo.
     */
    @Nullable
    public RefactoringInfo create(org.jetbrains.research.kotlinrminer.ide.Refactoring refactoring, String projectPath) {
        final KotlinRefactoringHandler handler = RefactoringType.valueOf(refactoring.getRefactoringType().name()).getKotlinHandler();
        try {
            return handler == null ? null : handler.handle(refactoring, projectPath)
                    .setType(refactoring.getRefactoringType().getDisplayName());
        } catch (RefactoringProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
