package org.jetbrains.research.refactorinsight.services;

import com.google.common.graph.EndpointPair;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import org.codetracker.api.CodeTracker;
import org.codetracker.api.Edge;
import org.codetracker.api.History;
import org.codetracker.api.MethodTracker;
import org.codetracker.change.Change;
import org.codetracker.element.Method;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.jetbrains.research.refactorinsight.adapters.CodeChange;

import java.util.ArrayList;
import java.util.List;

public class ChangeHistoryService {
    public List<CodeChange> getHistoryForMethod(String projectPath, String filePath,
                                                String methodName,
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
                                history = methodTracker.track();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return history;
                        }
                );

                for (EndpointPair<Method> edge : methodHistory.getGraph().getEdges()) {
                    Edge edgeValue = methodHistory.getGraph().getEdgeValue(edge).get();
                    for (Change change : edgeValue.getChangeList()) {
                        if (Change.Type.NO_CHANGE.equals(change.getType()))
                            continue;
                        String commitId = edge.target().getVersion().getId();
                        String changeType = change.getType().getTitle();
                        String changeDescription = change.toString();
                        changeHistory.add(new CodeChange(commitId, changeType, changeDescription));
                    }
                }
            }
        } catch (Exception e) {
            //TODO handle the exception
        }
        return changeHistory;
    }
}
