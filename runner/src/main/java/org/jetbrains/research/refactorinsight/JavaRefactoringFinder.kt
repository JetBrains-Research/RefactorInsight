package org.jetbrains.research.refactorinsight

import com.intellij.openapi.vfs.VirtualFile
import org.refactoringminer.api.Refactoring
import org.refactoringminer.api.RefactoringHandler
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl
import org.refactoringminer.util.GitServiceImpl

object JavaRefactoringFinder {
    fun findRefactoringsAtCommit(
        repoRoot: VirtualFile,
        commitHash: String,
        processRefactorings: (List<Refactoring>) -> Unit,
    ) {
        GitServiceImpl().openRepository(repoRoot.path).use { repo ->
            GitHistoryRefactoringMinerImpl().detectAtCommit(repo, commitHash,
                object : RefactoringHandler() {
                    override fun handle(
                        commitId: String,
                        refactorings: List<Refactoring>
                    ) {
                        processRefactorings(refactorings)
                    }
                })
        }
    }

    fun findRefactoringsBetweenCommits(
        repoRoot: VirtualFile,
        startCommitHash: String,
        endCommit: String,
        processRefactorings: (MutableList<Refactoring>) -> Unit,
    ) {
        GitServiceImpl().openRepository(repoRoot.path).use { repo ->
            GitHistoryRefactoringMinerImpl().detectBetweenCommits(repo,
                startCommitHash,
                endCommit,
                object : RefactoringHandler() {
                    override fun handle(
                        commitId: String,
                        refactorings: MutableList<Refactoring>
                    ) {
                        processRefactorings(refactorings)
                    }
                }
            )
        }
    }
}