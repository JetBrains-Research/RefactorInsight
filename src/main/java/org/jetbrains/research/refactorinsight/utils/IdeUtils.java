package org.jetbrains.research.refactorinsight.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.util.Ref;
import com.intellij.util.ExceptionUtil;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class IdeUtils {
  /**
   * Allows to interrupt a process which does not performs checkCancelled() calls by itself.
   */
  public static void runWithCheckCanceled(@NotNull final Runnable runnable,
                                          @NotNull final ProgressIndicator indicator) throws Exception {
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
      runWithCheckCanceled(future, indicator);
      ExceptionUtil.rethrowAll(error.get());
    } catch (ProcessCanceledException e) {
      future.cancel(true);
      throw e;
    }
  }

  /**
   * Waits for {@code future} to be complete, or the current thread's indicator to be canceled.
   */
  private static <T> void runWithCheckCanceled(@NotNull Future<T> future,
                                               @NotNull final ProgressIndicator indicator) throws ExecutionException {
    while (true) {
      indicator.checkCanceled();
      try {
        future.get(10, TimeUnit.MILLISECONDS);
        return;
      } catch (InterruptedException e) {
        throw new ProcessCanceledException(e);
      } catch (TimeoutException ignored) {
        ignored.printStackTrace();
      }
    }
  }
}
