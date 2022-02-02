package org.jetbrains.research.refactorinsight.services;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import gr.uom.java.xmi.LocationInfo;
import org.codetracker.api.*;
import org.codetracker.change.Change;
import org.codetracker.element.Attribute;
import org.codetracker.element.Method;
import org.codetracker.element.Variable;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.adapters.CodeChange;
import org.jetbrains.research.refactorinsight.ui.windows.ElementType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChangeHistoryService {
    private static final Logger LOG = Logger.getInstance(ChangeHistoryService.class);

    public List<CodeChange> getHistoryForMethod(@NotNull Project project,
                                                @NotNull String projectPath,
                                                @NotNull String filePath,
                                                @NotNull String methodName,
                                                int methodDeclarationLine) {
        List<CodeChange> changeHistory = new ArrayList<>();
        try (Repository repository = MiningService.openRepository(projectPath)) {
            if (repository != null) {
                String latestCommitHash = getLatestCommitHash(repository);
                MethodTracker methodTracker = CodeTracker.methodTracker()
                        .repository(repository)
                        .filePath(filePath)
                        .startCommitId(latestCommitHash)
                        .methodName(methodName)
                        .methodDeclarationLineNumber(methodDeclarationLine)
                        .build();

                ProgressManager.getInstance().run(new Task.Modal(project, "Mining the change history for a method", true) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        History<Method> methodHistory = null;
                        try {
                            LOG.info("[RefactorInsight]: Start tracking method's history using CodeTracker.");
                            methodHistory = methodTracker.track();
                        } catch (Exception e) {
                            LOG.error(String.format("[RefactorInsight]: Error occurred while tracking method's"
                                    + " history using CodeTracker. Details: %s", e.getMessage()), e);
                        }

                        for (History.HistoryInfo<Method> historyInfo : methodHistory.getHistoryInfoList()) {
                            String commitId = historyInfo.getCommitId();
                            String changeDate = formatDate(historyInfo.getCommitTime());
                            String changeAuthor = historyInfo.getCommitterName();
                            LocationInfo locationBefore = historyInfo.getElementBefore().getLocation();
                            LocationInfo locationAfter = historyInfo.getElementAfter().getLocation();

                            for (Change change : historyInfo.getChangeList()) {
                                Change.Type changeType = change.getType();
                                String changeDescription = change.toString();
                                changeHistory.add(new CodeChange(commitId, changeType, changeDescription, changeDate,
                                        changeAuthor, locationBefore, locationAfter, ElementType.METHOD));
                            }
                        }
                    }
                });
            }

            return changeHistory;
        }
    }

    public List<CodeChange> getHistoryForVariable(@NotNull Project project,
                                                  @NotNull String projectPath,
                                                  @NotNull String filePath,
                                                  @NotNull String methodName,
                                                  int methodDeclarationLine,
                                                  @NotNull String variableName,
                                                  int variableDeclarationLine) {
        List<CodeChange> changeHistory = new ArrayList<>();
        try (Repository repository = MiningService.openRepository(projectPath)) {
            if (repository != null) {
                String latestCommitHash = getLatestCommitHash(repository);
                VariableTracker variableTracker = CodeTracker.variableTracker()
                        .repository(repository)
                        .filePath(filePath)
                        .startCommitId(latestCommitHash)
                        .methodName(methodName)
                        .methodDeclarationLineNumber(methodDeclarationLine)
                        .variableName(variableName)
                        .variableDeclarationLineNumber(variableDeclarationLine)
                        .build();

                ProgressManager.getInstance().run(new Task.Modal(project, "Mining the change history for a variable", true) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        History<Variable> variableHistory = null;
                        try {
                            LOG.info("[RefactorInsight]: Start tracking variable's history using CodeTracker.");
                            variableHistory = variableTracker.track();
                        } catch (Exception ex) {
                            LOG.error(String.format("[RefactorInsight]: Error occurred while tracking variable's" +
                                    " history using CodeTracker. Details: %s", ex.getMessage()), ex);
                        }
                        for (History.HistoryInfo<Variable> historyInfo : variableHistory.getHistoryInfoList()) {
                            for (Change change : historyInfo.getChangeList()) {
                                String commitId = historyInfo.getCommitId();
                                String changeDate = formatDate(historyInfo.getCommitTime());
                                String changeAuthor = historyInfo.getCommitterName();
                                Change.Type changeType = change.getType();
                                String changeDescription = change.toString();
                                LocationInfo sourceLocation = historyInfo.getElementBefore().getLocation();
                                LocationInfo targetLocation = historyInfo.getElementAfter().getLocation();
                                changeHistory.add(new CodeChange(commitId, changeType, changeDescription,
                                        changeDate, changeAuthor, sourceLocation, targetLocation, ElementType.VARIABLE));
                            }
                        }

                    }
                });
            }
        }
        return changeHistory;
    }

    public List<CodeChange> getHistoryForAttribute(@NotNull Project project,
                                                   @NotNull String projectPath,
                                                   @NotNull String filePath,
                                                   @NotNull String attributeName,
                                                   int attributeDeclarationLine) {
        List<CodeChange> changeHistory = new ArrayList<>();
        try (Repository repository = MiningService.openRepository(projectPath)) {
            if (repository != null) {
                String latestCommitHash = getLatestCommitHash(repository);
                AttributeTracker attributeTracker = CodeTracker.attributeTracker()
                        .repository(repository)
                        .filePath(filePath)
                        .startCommitId(latestCommitHash)
                        .attributeName(attributeName)
                        .attributeDeclarationLineNumber(attributeDeclarationLine)
                        .build();


                ProgressManager.getInstance().run(new Task.Modal(project, "Mining the change history for a field", true) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        History<Attribute> attributeHistory = null;
                        try {
                            LOG.info("[RefactorInsight]: Start tracking field's history using CodeTracker.");
                            attributeHistory = attributeTracker.track();
                        } catch (Exception e) {
                            LOG.error(String.format("[RefactorInsight]: Error occurred while tracking field's" +
                                    " history using CodeTracker. Details: %s", e.getMessage()), e);
                        }
                        for (History.HistoryInfo<Attribute> historyInfo : attributeHistory.getHistoryInfoList()) {
                            for (Change change : historyInfo.getChangeList()) {
                                String commitId = historyInfo.getCommitId();
                                String changeDate = formatDate(historyInfo.getCommitTime());
                                String changeAuthor = historyInfo.getCommitterName();
                                Change.Type changeType = change.getType();
                                String changeDescription = change.toString();
                                LocationInfo sourceLocation = historyInfo.getElementBefore().getLocation();
                                LocationInfo targetLocation = historyInfo.getElementAfter().getLocation();
                                changeHistory.add(new CodeChange(commitId, changeType, changeDescription,
                                        changeDate, changeAuthor, sourceLocation, targetLocation, ElementType.ATTRIBUTE));
                            }
                        }
                    }
                });
            }
        }
        return changeHistory;
    }

    @NotNull
    private String getLatestCommitHash(Repository repository) {
        RevCommit latestCommit = null;
        try {
            latestCommit = new Git(repository).log().setMaxCount(1).call().iterator().next();
        } catch (GitAPIException e) {
            LOG.error(String.format("[RefactorInsight]: Error occurred while getting the latest commit. Details: %s", e.getMessage()), e);
        }
        return latestCommit.getName();
    }

    private String formatDate(long time) {
        return LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("d/M/yy, h:mm a"));
    }
}
