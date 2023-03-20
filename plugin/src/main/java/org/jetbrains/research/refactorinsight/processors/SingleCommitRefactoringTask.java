package org.jetbrains.research.refactorinsight.processors;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.vcs.log.VcsCommitMetadata;
import org.eclipse.jgit.lib.Repository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.research.refactorinsight.RefactorInsightBundle;
import org.jetbrains.research.refactorinsight.services.MiningService;
import org.jetbrains.research.refactorinsight.ui.windows.GitWindow;

public class SingleCommitRefactoringTask extends CancellableRefactoringMiningTask {
    private static final Logger logger = Logger.getInstance(SingleCommitRefactoringTask.class);
    private final Project project;
    private final VcsCommitMetadata commit;
    private final GitWindow window;
    private final MiningService service;
    private final Repository myRepository;

    /**
     * Cancelable mining task for mining a single commit.
     *
     * @param project Current IDEA project
     * @param commit  Commit meta data
     * @param window  Git Window for callback
     */
    public SingleCommitRefactoringTask(
            @Nullable Project project,
            VcsCommitMetadata commit,
            GitWindow window) {
        super(project,
                String.format(RefactorInsightBundle.message("detect.at"), commit.getId().toShortString()));
        this.project = project;
        this.commit = commit;
        this.window = window;
        this.service = ServiceManager.getService(project, MiningService.class);
        this.myRepository = service.getRepository();
    }

    @Override
    public void onFinished() {
        super.onFinished();
        if (service.containsCommit(commit.getId().asString())) {
            System.out.println(RefactorInsightBundle.message("finished"));
            ApplicationManager.getApplication()
                    .invokeLater(() -> window.refresh(commit.getId().asString()));
        }
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {
        try {
            runWithCheckCanceled(
                    CommitMiner.mineAtCommit(commit.getId().asString(), commit.getParents().get(0).asString(),
                            commit.getTimestamp(), service.getState().refactoringsMap.map, project,
                            myRepository),
                    progressIndicator, commit, project
            );
        } catch (Exception e) {
            logger.info(String.format("The mining of refactorings at the commit %s was canceled", commit.getId()));
        }
    }

    @Override
    public String toString() {
        return "SingleCommitRefactoringTask " + commit.getId().asString();
    }
}