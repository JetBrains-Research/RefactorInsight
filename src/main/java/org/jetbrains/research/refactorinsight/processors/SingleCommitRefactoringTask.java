package org.jetbrains.research.refactorinsight.processors;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.util.ExceptionUtil;
import com.intellij.vcs.log.TimedVcsCommit;
import com.intellij.vcs.log.VcsCommitMetadata;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.jgit.lib.Repository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.research.refactorinsight.RefactorInsightBundle;
import org.jetbrains.research.refactorinsight.data.RefactoringEntry;
import org.jetbrains.research.refactorinsight.services.MiningService;
import org.jetbrains.research.refactorinsight.ui.windows.GitWindow;

public class SingleCommitRefactoringTask extends Task.Backgroundable {

  private final Project project;
  private final VcsCommitMetadata commit;
  private final GitWindow window;
  private final MiningService service;
  private final Repository myRepository;
  private boolean canceled = false;
  private final Logger logger = Logger.getInstance(SingleCommitRefactoringTask.class);

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
        String.format(RefactorInsightBundle.message("mining.at"), commit.getId().toShortString()));
    this.project = project;
    this.commit = commit;
    this.window = window;
    this.service = ServiceManager.getService(project, MiningService.class);
    this.myRepository = service.getRepository();
  }

  @Override
  public void onCancel() {
    super.onCancel();
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
          () -> CommitMiner.mineAtCommit(commit, service.getState().refactoringsMap.map, project, myRepository),
          progressIndicator, commit, project
      );
    } catch (Exception e) {
      logger.info(String.format("The mining of refactorings at the commit %s was canceled", commit.getId()));
    }
  }

  public void cancel() {
    canceled = true;
  }

  /**
   * Allows to interrupt a process which does not performs checkCancelled() calls by itself.
   */
  public void runWithCheckCanceled(@NotNull final Runnable runnable,
                                          @NotNull final ProgressIndicator indicator,
                                          VcsCommitMetadata commit, Project project) throws Exception {
    final Ref<Throwable> error = Ref.create();
    Future<?> future = ApplicationManager.getApplication().executeOnPooledThread(
        () -> ProgressManager.getInstance().executeProcessUnderProgress(() -> {
          try {
            runnable.run();
          } catch (Throwable t) {
            error.set(t);
          }
        }, indicator)
    );
    try {
      runWithCheckCanceled(future, indicator, commit, project);
      ExceptionUtil.rethrowAll(error.get());
    } catch (ProcessCanceledException e) {
      future.cancel(true);
      throw e;
    }
  }

  /**
   * Waits for {@code future} to be complete or reach the maximum allowed mining time of 60 sec,
   * or the current thread's indicator to be canceled.
   */
  private <T> void runWithCheckCanceled(@NotNull Future<T> future,
                                        @NotNull final ProgressIndicator indicator,
                                        TimedVcsCommit commit, Project project) throws
      ExecutionException {
    int timeout = 6000;
    while (timeout > 0) {
      if (canceled) {
        indicator.cancel();
      }
      indicator.checkCanceled();
      try {
        future.get(10, TimeUnit.MILLISECONDS);
        return;
      } catch (InterruptedException e) {
        throw new ProcessCanceledException(e);
      } catch (TimeoutException ignored) {
        logger.info("The timeout has been exceeded while checking task cancellation");
      }
      timeout -= 1;
    }
    if (timeout == 0) {
      RefactoringEntry refactoringEntry = RefactoringEntry
          .convert(new ArrayList<>(), commit, project);
      refactoringEntry.setTimeout(true);
      MiningService.getInstance(project).getState().refactoringsMap.map.put(commit.getId().asString(),
          refactoringEntry);
    }
  }
}