package org.jetbrains.research.refactorinsight.processors;

import org.jetbrains.research.refactorinsight.RefactoringProcessingException;
import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.KotlinRefactoringHandler;
import org.refactoringminer.api.Refactoring;

/**
 * Creates {@link RefactoringInfo} objects for refactorings provided by RefactoringMiner and kotlinRMiner.
 */
public class InfoFactory {

    /**
     * Creates a relevant {@link RefactoringInfo} instance for a given Refactoring provided by RefactoringMiner.
     *
     * @param refactoring to be analyzed.
     * @return resulting RefactoringInfo.
     */
    public RefactoringInfo create(Refactoring refactoring) {
        final JavaRefactoringHandler handler = RefactoringType.valueOf(refactoring.getRefactoringType().name()).getJavaHandler();
        RefactoringInfo refactoringInfo = handler.handle(refactoring);
        refactoringInfo.setType(refactoring.getRefactoringType().getDisplayName());
        return refactoringInfo;
    }

    /**
     * Creates a relevant {@link RefactoringInfo} instance for a given Refactoring provided by kotlinRMiner.
     *
     * @param refactoring to be analyzed.
     * @return resulting RefactoringInfo.
     */
    public RefactoringInfo create(org.jetbrains.research.kotlinrminer.ide.Refactoring refactoring) throws RefactoringProcessingException {
        final KotlinRefactoringHandler handler = RefactoringType.valueOf(refactoring.getRefactoringType().name()).getKotlinHandler();
        RefactoringInfo refactoringInfo = handler.handle(refactoring);
        refactoringInfo.setType(refactoring.getRefactoringType().getDisplayName());
        return refactoringInfo;
    }

}
