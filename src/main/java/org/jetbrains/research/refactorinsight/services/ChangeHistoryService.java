package org.jetbrains.research.refactorinsight.services;

//import org.codetracker.api.MethodTracker;

import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitService;
import org.refactoringminer.util.GitServiceImpl;

public class ChangeHistoryService {
    public void getHistoryForMethod(String projectPath, String filePath, String methodName,
                                    String startCommitId, int methodDeclarationLine) {
        GitService gitService = new GitServiceImpl();
        try (Repository repository = MiningService.openRepository(projectPath)) {
            //TODO: build CodeTracker jar and use it or wait until it is published in the maven repository
/*            MethodTracker methodTracker = ChangeHistoryService.methodTracker()
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
                    String commitId = edge.target().getVersion().getId();
                    String changeType = change.getType().getTitle();
                    String changeDescription = change.toString();
                    System.out.printf("%s,%s,%s%n", commitId, changeType, change);
                }
            }*/
        }
    }
}
