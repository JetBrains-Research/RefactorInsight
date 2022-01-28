package org.jetbrains.research.refactorinsight.services;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import gr.uom.java.xmi.LocationInfo;
import org.codetracker.api.*;
import org.codetracker.change.Change;
import org.codetracker.element.Attribute;
import org.codetracker.element.Method;
import org.codetracker.element.Variable;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.adapters.CodeChange;
import com.intellij.openapi.diagnostic.Logger;

import java.util.ArrayList;
import java.util.List;

public class ChangeHistoryService {
    private static final Logger LOG = Logger.getInstance(ChangeHistoryService.class);

    public List<CodeChange> getHistoryForMethod(@NotNull String projectPath,
                                                @NotNull String filePath,
                                                @NotNull String methodName,
                                                int methodDeclarationLine) {
        List<CodeChange> changeHistory = new ArrayList<>();
        try (Repository repository = MiningService.openRepository(projectPath)) {
            if (repository != null) {
                RevCommit latestCommit = new Git(repository).log().setMaxCount(1).call().iterator().next();
                String latestCommitHash = latestCommit.getName();
                MethodTracker methodTracker = CodeTracker.methodTracker()
                        .repository(repository)
                        .filePath(filePath)
                        .startCommitId(latestCommitHash)
                        .methodName(methodName)
                        .methodDeclarationLineNumber(methodDeclarationLine)
                        .build();

                History<Method> methodHistory = ApplicationManager.getApplication().runReadAction(
                        (Computable<? extends History<Method>>) () -> {
                            History<Method> history = null;
                            try {
                                LOG.info("[RefactorInsight]: Start tracking method's history using CodeTracker.");
                                history = methodTracker.track();
                            } catch (Exception e) {
                                LOG.error(String.format("[RefactorInsight]: Error occurred while tracking method's" +
                                        " history using CodeTracker. Details: %s", e.getMessage()), e);
                            }
                            return history;
                        }
                );

                for (History.HistoryInfo<Method> historyInfo : methodHistory.getHistoryInfoList()) {
                    String commitId = historyInfo.getCommitId();
                    LocationInfo locationBefore = historyInfo.getElementBefore().getLocation();
                    LocationInfo locationAfter = historyInfo.getElementAfter().getLocation();

                    for (Change change : historyInfo.getChangeList()) {
                        String changeType = change.getType().getTitle();
                        String changeDescription = change.toString();
                        changeHistory.add(new CodeChange(commitId, changeType, changeDescription, locationBefore, locationAfter));
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(String.format("[RefactorInsight]: Error occurred while tracking method's" +
                    " history using CodeTracker. Details: %s", e.getMessage()), e);
        }
        return changeHistory;
    }

    public List<CodeChange> getHistoryForVariable(@NotNull String projectPath,
                                                  @NotNull String filePath,
                                                  @NotNull String methodName,
                                                  int methodDeclarationLine,
                                                  @NotNull String variableName,
                                                  int variableDeclarationLine) {
        List<CodeChange> changeHistory = new ArrayList<>();
        try (Repository repository = MiningService.openRepository(projectPath)) {
            if (repository != null) {
                RevCommit latestCommit = new Git(repository).log().setMaxCount(1).call().iterator().next();
                String latestCommitHash = latestCommit.getName();

                VariableTracker variableTracker = CodeTracker.variableTracker()
                        .repository(repository)
                        .filePath(filePath)
                        .startCommitId(latestCommitHash)
                        .methodName(methodName)
                        .methodDeclarationLineNumber(methodDeclarationLine)
                        .variableName(variableName)
                        .variableDeclarationLineNumber(variableDeclarationLine)
                        .build();

                LOG.info("[RefactorInsight]: Start tracking variable's history using CodeTracker.");

                History<Variable> variableHistory = variableTracker.track();
                for (History.HistoryInfo<Variable> historyInfo : variableHistory.getHistoryInfoList()) {
                    for (Change change : historyInfo.getChangeList()) {
                        String commitId = historyInfo.getCommitId();
                        String changeType = change.getType().getTitle();
                        String changeDescription = change.toString();
                        LocationInfo sourceLocation = historyInfo.getElementBefore().getLocation();
                        LocationInfo targetLocation = historyInfo.getElementAfter().getLocation();
                        changeHistory.add(new CodeChange(commitId, changeType, changeDescription, sourceLocation, targetLocation));
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(String.format("[RefactorInsight]: Error occurred while tracking variable's" +
                    " history using CodeTracker. Details: %s", e.getMessage()), e);
        }
        return changeHistory;
    }

    public List<CodeChange> getHistoryForAttribute(@NotNull String projectPath,
                                                   @NotNull String filePath,
                                                   @NotNull String attributeName,
                                                   int attributeDeclarationLine) {
        List<CodeChange> changeHistory = new ArrayList<>();
        try (Repository repository = MiningService.openRepository(projectPath)) {
            if (repository != null) {
                RevCommit latestCommit = new Git(repository).log().setMaxCount(1).call().iterator().next();
                String latestCommitHash = latestCommit.getName();

                AttributeTracker attributeTracker = CodeTracker.attributeTracker()
                        .repository(repository)
                        .filePath(filePath)
                        .startCommitId(latestCommitHash)
                        .attributeName(attributeName)
                        .attributeDeclarationLineNumber(attributeDeclarationLine)
                        .build();

                LOG.info("[RefactorInsight]: Start tracking field's history using CodeTracker.");

                History<Attribute> attributeHistory = attributeTracker.track();
                for (History.HistoryInfo<Attribute> historyInfo : attributeHistory.getHistoryInfoList()) {
                    for (Change change : historyInfo.getChangeList()) {
                        String commitId = historyInfo.getCommitId();
                        String changeType = change.getType().getTitle();
                        String changeDescription = change.toString();
                        LocationInfo sourceLocation = historyInfo.getElementBefore().getLocation();
                        LocationInfo targetLocation = historyInfo.getElementAfter().getLocation();
                        changeHistory.add(new CodeChange(commitId, changeType, changeDescription, sourceLocation, targetLocation));
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(String.format("[RefactorInsight]: Error occurred while tracking field's" +
                    " history using CodeTracker. Details: %s", e.getMessage()), e);
        }
        return changeHistory;
    }
}
