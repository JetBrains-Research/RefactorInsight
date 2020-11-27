package org.jetbrains.research.refactorinsight.processors;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.eclipse.jgit.lib.Repository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.research.refactorinsight.RefactorInsightBundle;
import org.jetbrains.research.refactorinsight.services.MiningService;

import javax.swing.JScrollPane;

public class PRMiningBackgroundableTask extends Task.Backgroundable {
  private final Project project;
  private final String commit;
  private final JScrollPane panel;
  private final MiningService service;
  private final Repository myRepository;
  private final String commitParentHash;
  private final long commitTimestamp;

  /**
   * Cancelable mining task for mining a single commit.
   *
   * @param project    current  project.
   * @param commitHash commit hash.
   */
  public PRMiningBackgroundableTask(
      @Nullable Project project,
      String commitHash, String commitParentHash, long commitTimestamp, JScrollPane panel) {
    super(project,
        String.format(RefactorInsightBundle.message("mining.at"), commitHash));
    this.project = project;
    this.commit = commitHash;
    this.service = ServiceManager.getService(project, MiningService.class);
    this.myRepository = service.getRepository();
    this.panel = panel;
    this.commitParentHash = commitParentHash;
    this.commitTimestamp = commitTimestamp;
  }

  @Override
  public void onCancel() {
    super.onCancel();
  }

  @Override
  public void onFinished() {
    super.onFinished();
    if (service.containsCommit(commit)) {
      ApplicationManager.getApplication()
          .invokeLater(panel::updateUI);
    }
  }

  @Override
  public void run(@NotNull ProgressIndicator progressIndicator) {
    CommitMiner.mineAtCommit(commit, commitParentHash, commitTimestamp,
        service.getState().refactoringsMap.map, project, myRepository);

  }

}
