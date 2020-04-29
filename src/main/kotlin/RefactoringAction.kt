import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.vcs.ProjectLevelVcsManager
import com.intellij.openapi.vcs.VcsRoot
import com.intellij.openapi.vcs.impl.ProjectLevelVcsManagerImpl
import git4idea.repo.GitRepositoryManager
import org.refactoringminer.api.GitHistoryRefactoringMiner
import org.refactoringminer.api.GitService
import org.refactoringminer.api.Refactoring
import org.refactoringminer.api.RefactoringHandler
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl
import org.refactoringminer.util.GitServiceImpl
import java.util.*
import kotlin.collections.ArrayList


class RefactoringAction : AnAction() {

    /**
     * Implement this method to provide your action handler.
     *
     * @param e Carries information on the invocation place
     */
    override fun actionPerformed(e: AnActionEvent) {
        var currentProject = e.project
        val map = currentProject!!.service<StoringService>().state!!.map

        val instance =
            ProjectLevelVcsManager.getInstance(currentProject) as ProjectLevelVcsManagerImpl
        val gitRootPath = Arrays.stream(instance.allVcsRoots)
            .filter { x: VcsRoot -> x.vcs != null }
            .filter { x: VcsRoot ->
                x.vcs!!.name.equals("git", ignoreCase = true)
            }
            .findAny().orElse(null)

        if (gitRootPath == null) {
            println("no repo")
        } else {
            val branch =  GitRepositoryManager.getInstance(currentProject!!).repositories.stream()
                .filter { x -> x.root.equals(gitRootPath.path) }
                .map{x -> x.currentBranch}
                .map{x -> x!!.name}
                .findFirst().orElse("master")

            println("branch name $branch")

            val gitService: GitService = GitServiceImpl()
            val miner: GitHistoryRefactoringMiner = GitHistoryRefactoringMinerImpl()

            println(gitService.openRepository(currentProject.basePath))
            miner.detectAll(
                gitService.openRepository(currentProject.basePath),
                branch,
                object : RefactoringHandler() {
                    override fun handle(
                        commitId: String,
                        refactorings: List<Refactoring>
                    ) {
                        val refs = ArrayList<String>()
                        map[commitId] = refs
                        for (ref in refactorings) {
                            refs.add(ref.name)
                        }
                    }
                })
        }

    }
}
