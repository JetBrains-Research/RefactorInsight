package org.jetbrains.research.refactorinsight.processors;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.vcs.log.VcsFullCommitDetails;
import org.eclipse.jgit.lib.Repository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.research.refactorinsight.RefactorInsightBundle;
import org.jetbrains.research.refactorinsight.pullrequests.PRFileEditor;
import org.jetbrains.research.refactorinsight.services.MiningService;

import java.util.List;

public class PRMiningBackgroundableTask extends CancellableRefactoringMiningTask {
    private static final Logger logger = Logger.getInstance(PRMiningBackgroundableTask.class);
    private final Project project;
    private final PRFileEditor prFileEditor;
    private final MiningService service;
    private final Repository myRepository;
    List<VcsFullCommitDetails> commitDetails;

    /**
     * Cancelable mining task for mining refactorings in pull request.
     *
     * @param project       current  project.
     * @param commitDetails pull request's commits details.
     */
    public PRMiningBackgroundableTask(
            @Nullable Project project, List<VcsFullCommitDetails> commitDetails, PRFileEditor prFileEditor) {
        super(project, RefactorInsightBundle.message("mining"));
        this.project = project;
        this.service = ServiceManager.getService(project, MiningService.class);
        this.myRepository = service.getRepository();
        this.prFileEditor = prFileEditor;
        this.commitDetails = commitDetails;
    }

    @Override
    public void onFinished() {
        super.onFinished();
        ApplicationManager.getApplication()
                .invokeLater(prFileEditor::buildComponent);
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {
        for (VcsFullCommitDetails commit : commitDetails) {
            try {
                runWithCheckCanceled(
                        CommitMiner.mineAtCommit(commit.getId().asString(), commit.getParents().get(0).asString(),
                                commit.getTimestamp(), service.getState().refactoringsMap.map, project,
                                myRepository),
                        progressIndicator, commit, project
                );
            } catch (Exception e) {
                logger.info(String.format("The mining of refactorings at the commit %s was canceled",
                        commit.getId().asString()));
            }
        }
    }

    @Override
    public String toString() {
        return "PRMiningBackgroundableTask " + commitDetails.toString();
    }
}
