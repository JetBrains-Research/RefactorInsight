package org.jetbrains.research.refactorinsight.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.util.ExceptionUtil;
import com.intellij.vcs.log.VcsCommitMetadata;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.jetbrains.research.refactorinsight.data.RefactoringEntry;
import org.jetbrains.research.refactorinsight.services.MiningService;

public class IdeUtils {

  private static final Logger logger = Logger.getInstance(IdeUtils.class);

  /**
   * Allows to interrupt a process which does not performs checkCancelled() calls by itself.
   */
  public static void runWithCheckCanceled(@NotNull final Runnable runnable,
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
  private static <T> void runWithCheckCanceled(@NotNull Future<T> future,
                                               @NotNull final ProgressIndicator indicator,
                                               VcsCommitMetadata commit, Project project) throws ExecutionException {
    int timeout = 6000;
    while (timeout > 0) {
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
