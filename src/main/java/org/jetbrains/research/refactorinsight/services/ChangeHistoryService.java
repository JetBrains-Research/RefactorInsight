package org.jetbrains.research.refactorinsight.services;

import com.google.common.graph.EndpointPair;
import org.codetracker.api.CodeTracker;
import org.codetracker.api.Edge;
import org.codetracker.api.History;
import org.codetracker.api.MethodTracker;
import org.codetracker.change.Change;
import org.codetracker.element.Method;
import org.eclipse.jgit.lib.Repository;

import java.util.ArrayList;
import java.util.List;

public class ChangeHistoryService {
    public List<String> getHistoryForMethod(String projectPath, String filePath, String methodName,
                                            String startCommitId, int methodDeclarationLine) {
        List<String> changeHistory = new ArrayList<>();
        try (Repository repository = MiningService.openRepository(projectPath)) {
            MethodTracker methodTracker = CodeTracker.methodTracker()
                    .repository(repository)
                    .filePath(filePath)
                    .startCommitId(startCommitId)
                    .methodName(methodName)
                    .methodDeclarationLineNumber(methodDeclarationLine)
                    .build();

            History<Method> methodHistory = methodTracker.track();

            for (EndpointPair<Method> edge : methodHistory.getGraph().getEdges()) {
                Edge edgeValue = methodHistory.getGraph().getEdgeValue(edge).get();
                for (Change change : edgeValue.getChangeList()) {
                    if (Change.Type.NO_CHANGE.equals(change.getType()))
                        continue;
                    String changeDescription = change.toString();
                    changeHistory.add(changeDescription);
                }
            }
        } catch (Exception e) {
            //TODO handle the exception
        }
        return changeHistory;
    }
}
