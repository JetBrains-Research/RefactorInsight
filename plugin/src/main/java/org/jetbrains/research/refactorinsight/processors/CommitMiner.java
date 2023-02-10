package org.jetbrains.research.refactorinsight.processors;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.util.Consumer;
import com.intellij.vcs.log.TimedVcsCommit;
import git4idea.history.GitCommitRequirements;
import git4idea.history.GitLogUtil;
import git4idea.repo.GitRepository;
import org.eclipse.jgit.lib.Repository;
import org.jetbrains.research.kotlinrminer.ide.KotlinRMiner;
import org.jetbrains.research.refactorinsight.RefactorInsightBundle;
import org.jetbrains.research.refactorinsight.data.RefactoringEntry;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.services.MiningService;
import org.jetbrains.research.refactorinsight.utils.TextUtils;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The CommitMiner is a Consumer of GitCommit.
 * It mines a commit and updates the refactoring map with the data retrieved for that commit.
 * Consumes a git commit, calls RefactoringMiner and detects the refactorings for a commit.
 */
public class CommitMiner implements Consumer<TimedVcsCommit> {
    public static final InfoFactory INFO_FACTORY = new InfoFactory();
    private static final String progress = RefactorInsightBundle.message("progress");
    private final ExecutorService pool;
    private final Map<String, RefactoringEntry> map;
    private final Project myProject;
    private final Repository myRepository;
    private final AtomicInteger commitsDone;
    private final ProgressIndicator progressIndicator;
    private final int limit;

    /**
     * CommitMiner for mining a single commit.
     *
     * @param pool       ThreadPool to submit to.
     * @param map        Map to add mined commit data to.
     * @param repository GitRepository.
     */
    public CommitMiner(ExecutorService pool, Map<String, RefactoringEntry> map,
                       GitRepository repository,
                       AtomicInteger commitsDone, ProgressIndicator progressIndicator, int limit) {
        this.pool = pool;
        this.map = map;
        myProject = repository.getProject();
        //NB: nullable, check if initialized correctly
        myRepository = ServiceManager.getService(myProject, MiningService.class).getRepository();
        this.commitsDone = commitsDone;
        this.progressIndicator = progressIndicator;
        this.limit = limit;
    }

    /**
     * Returns a runnable that processes only one commit by consistently running RefactoringMiner and kotlinRMiner.
     *
     * @param commitHash       commit hash.
     * @param commitParentHash commit parent's hash.
     * @param commitTimestamp  commit timestamp.
     * @param map              the inner map that should be updated.
     * @param project          the current project.
     * @param repository       Git Repository.
     */
    public static Runnable mineAtCommit(String commitHash, String commitParentHash, long commitTimestamp,
                                        Map<String, RefactoringEntry> map,
                                        Project project, Repository repository) {
        return getRunnableToDetectRefactorings(map, commitHash, commitParentHash, commitTimestamp, repository, project);
    }

    /**
     * Creates a runnable to detect refactorings in Kotlin and Java code.
     *
     * @param commitHash       commit hash.
     * @param commitParentHash commit parent's hash.
     * @param commitTimestamp  commit timestamp.
     * @param map              the inner map that should be updated.
     * @param project          the current project.
     * @param repository       Git Repository.
     * @return a runnable.
     */
    private static Runnable getRunnableToDetectRefactorings(Map<String, RefactoringEntry> map, String commitHash,
                                                            String commitParentHash, long commitTimestamp,
                                                            Repository repository, Project project) {
        return () -> {
            GitHistoryRefactoringMiner jminer = new GitHistoryRefactoringMinerImpl();

            try {
                jminer.detectAtCommit(repository, commitHash, new RefactoringHandler() {
                    @Override
                    public void handle(String commitId, List<Refactoring> refactorings) {
                        createRefactoringEntry(map, commitHash, commitParentHash, commitTimestamp, project, refactorings);
                    }
                });

                ApplicationManager.getApplication().executeOnPooledThread(() -> {
                    try {
                        List<Change> changes = new ArrayList<>();
                        GitLogUtil.readFullDetailsForHashes(project,
                                ProjectFileIndex.getInstance(project).getContentRootForFile(project.getProjectFile()),
                                Collections.singletonList(commitHash),
                                GitCommitRequirements.DEFAULT,
                                c -> changes.addAll(c.getChanges()));
                        ApplicationManager.getApplication().invokeLater(() -> {
                            var refactorings = KotlinRMiner.INSTANCE.detectRefactorings(project, changes);
                            refactorings.forEach(
                                    r -> createRefactoringEntry(map, commitHash, commitParentHash,
                                            commitTimestamp, project, refactorings)
                            );
                        });
                    } catch (VcsException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    private static <T> void createRefactoringEntry(Map<String, RefactoringEntry> map, String commitHash,
                                                   String commitParentHash, long commitTimestamp,
                                                   Project project, List<T> refactorings) {
        RefactoringEntry entry = new RefactoringEntry(commitHash, commitParentHash, commitTimestamp);
        List<RefactoringInfo> infos = refactorings.stream()
                .map(refactoring -> INFO_FACTORY.create(refactoring, project.getBasePath()))
                .filter(Objects::nonNull)
                .map(info -> info.setEntry(entry))
                .toList();

        entry.setRefactorings(infos).combineRelated();
        entry.getRefactorings().forEach(info -> TextUtils.check(info, project));
        map.put(commitHash, entry);
    }

    /**
     * Mines a gitCommit.
     * Method that calls RefactoringMiner and updates the refactoring map.
     *
     * @param gitCommit to be mined
     */
    public void consume(TimedVcsCommit gitCommit) throws ProcessCanceledException {
        String commitId = gitCommit.getId().asString();

        if (!map.containsKey(commitId)) {
            pool.execute(() -> {
                if (progressIndicator.isCanceled()) {
                    cancelProgress();
                    return;
                }

                String commitParentHash =
                        gitCommit.getParents().size() == 0 ? null : gitCommit.getParents().get(0).asString();
                detectRefactorings(getRunnableToDetectRefactorings(map, commitId, commitParentHash,
                                gitCommit.getTimestamp(),
                                myRepository, myProject),
                        gitCommit.getId().asString(),
                        commitParentHash,
                        gitCommit.getTimestamp());
                incrementProgress();
            });
        } else {
            incrementProgress();
            progressIndicator.checkCanceled();
        }
    }

    private void detectRefactorings(Runnable runnable, String commitHash,
                                    String commitParentHash, long commitTimestamp) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<?> f = null;
        try {
            f = service.submit(runnable);
            f.get(120, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            if (f.cancel(true)) {
                RefactoringEntry refactoringEntry =
                        RefactoringEntry.createEmptyEntry(commitHash, commitParentHash, commitTimestamp);
                refactoringEntry.setTimeout(true);
                map.put(commitHash, refactoringEntry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            service.shutdown();
        }
    }

    /**
     * Increments the progress bar with each mined commit.
     */
    private void incrementProgress() {
        final int nCommits = commitsDone.incrementAndGet();
        progressIndicator.setText(String.format(progress,
                nCommits, limit));
        progressIndicator.setFraction((float) nCommits / limit);
    }

    private void cancelProgress() {
        final int nCommits = commitsDone.incrementAndGet();
        progressIndicator.setFraction((float) nCommits / limit);
        progressIndicator.setText("Cancelling");
    }
}