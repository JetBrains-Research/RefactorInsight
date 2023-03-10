package org.jetbrains.research.refactorinsight.processors;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.Ref;
import com.intellij.util.ExceptionUtil;
import com.intellij.vcs.log.TimedVcsCommit;
import com.intellij.vcs.log.VcsCommitMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.research.refactorinsight.data.RefactoringEntry;
import org.jetbrains.research.refactorinsight.services.MiningService;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class CancellableRefactoringMiningTask extends Task.Backgroundable {
    protected static final Logger logger = Logger.getInstance(CancellableRefactoringMiningTask.class);
    private volatile boolean canceled = false;

    public CancellableRefactoringMiningTask(@Nullable Project project, @NlsContexts.ProgressTitle @NotNull String title) {
        super(project, title, true);
    }

    public void cancel() {
        canceled = true;
    }

    /**
     * Allows interrupting a process which does not perform checkCancelled() calls by itself.
     */
    protected void runWithCheckCanceled(@NotNull final Runnable runnable,
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
            if (!indicator.isCanceled()) {
                throw e;
            }
        }
    }

    /**
     * Waits for {@code future} to be complete or reach the maximum allowed mining time of 60 sec,
     * or the current thread's indicator to be canceled.
     */
    private <T> void runWithCheckCanceled(@NotNull Future<T> future,
                                          @NotNull final ProgressIndicator indicator,
                                          TimedVcsCommit commit, Project project) throws ExecutionException {
        long repeatUntil = System.nanoTime() + TimeUnit.MINUTES.toNanos(1);

        do {
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
        } while (System.nanoTime() < repeatUntil);

        RefactoringEntry refactoringEntry =
                RefactoringEntry.createEmptyEntry(commit.getId().asString(), commit.getParents().get(0).asString(),
                        commit.getTimestamp());
        refactoringEntry.setTimeout(true);
        MiningService.getInstance(project).getState().refactoringsMap.map.putIfAbsent(commit.getId().asString(), refactoringEntry);
    }
}
