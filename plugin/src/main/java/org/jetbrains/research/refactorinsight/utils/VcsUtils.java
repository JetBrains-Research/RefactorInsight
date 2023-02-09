package org.jetbrains.research.refactorinsight.utils;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.vcs.log.impl.VcsLogManager;
import com.intellij.vcs.log.impl.VcsProjectLog;
import git4idea.repo.GitRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;

public class VcsUtils {
    public static ToolWindowManager manager;
    /**
     * Used for storing and disposing the MainVcsLogs used for method history action.
     */
    private static final ArrayList<Disposable> logs = new ArrayList<>();

    /**
     * Method used for disposing the logs that were created and shown for the method history action.
     * Called when the project is closing.
     * Avoids memory leaks.
     */
    public static void dispose() {
        for (Disposable log : logs) {
            Disposer.dispose(log);
        }
    }

    /**
     * Adds a Disposable object to the list.
     *
     * @param log to add.
     */
    public static void add(Disposable log) {
        logs.add(log);
    }

    /**
     * Get the total number of commits in a repository.
     *
     * @param repository GitRepository
     * @return the number of commits
     * @throws IOException in case of a problem
     */
    public static int getCommitCount(GitRepository repository) throws IOException {
        Process process = Runtime.getRuntime().exec("git rev-list --all --count", null,
                new File(repository.getRoot().getCanonicalPath()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String output = reader.readLine();
        return Integer.parseInt(output);
    }

    /**
     * Disposes the Vcs Log panel.
     */
    public static void disposeWithVcsLogManager(@NotNull Project project, @NotNull Disposable disposable) {
        Disposable connectionDisposable = Disposer.newDisposable();
        project.getMessageBus().connect(connectionDisposable)
                .subscribe(VcsProjectLog.VCS_PROJECT_LOG_CHANGED, new VcsProjectLog.ProjectLogListener() {
                    @Override
                    public void logCreated(@NotNull VcsLogManager manager) {
                    }

                    @Override
                    public void logDisposed(@NotNull VcsLogManager manager) {
                        Disposer.dispose(connectionDisposable);
                        Disposer.dispose(disposable);
                    }
                });
    }
}
