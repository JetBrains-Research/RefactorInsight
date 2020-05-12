import com.google.gson.Gson;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.vcsUtil.VcsUtil;
import git4idea.commands.Git;
import git4idea.commands.GitCommand;
import git4idea.commands.GitLineHandler;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class RefactoringAction extends AnAction {

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    GitRepository repository =
        GitRepositoryManager.getInstance(e.getProject()).getRepositories().get(0);

    ConcurrentHashMap<String, List<String>> methodsMap =
        e.getProject().getService(MethodService.class).getState().map;

    e.getProject().getService(MiningService.class)
        .mineRepo(repository, methodsMap);

    GitLineHandler handler = new GitLineHandler(e.getProject(),
        VcsUtil.getVirtualFile(e.getProject().getBasePath()), GitCommand.REV_PARSE);
    handler.addParameters("HEAD");
    String hash = Git.getInstance().runCommand(handler).getOutput().get(0);

    System.out.println(hash);
    Map<String, String> map = e.getProject().getService(MiningService.class).getState().map;
    List<RefactoringInfo> refactoringInfos = new ArrayList<>();
    visitOrderedCommits(hash, refactoringInfos, map);
    addMethodsRefactorings(methodsMap, refactoringInfos);
  }

  private void visitOrderedCommits(String commitId, List<RefactoringInfo> refactoringInfos,
                                   Map<String, String> map) {
    if (!map.containsKey(commitId)) {
      return;
    }
    RefactoringEntry refactoringEntry = RefactoringEntry.fromString(map.get(commitId));
    refactoringInfos.addAll(refactoringEntry.getRefactorings());
    visitOrderedCommits(refactoringEntry.getParentCommit(), refactoringInfos, map);
  }

  /**
   * Helper method for storing the refactorings for methods using the refactoring
   * processor.
   *
   * @param methodsMap the service that stores the methods refactoring history.
   * @param refs       the refactorings to be stored.
   */
  private void addMethodsRefactorings(Map<String, List<String>> methodsMap,
                                      List<RefactoringInfo> refs) {
    refs =
        refs.stream().filter(x -> !x.getSignatureAfter().equals("")).collect(Collectors.toList());
    Collections.reverse(refs);
    for (RefactoringInfo ref : refs) {
      List<String> refsBefore = new ArrayList<>();
      refsBefore
          .addAll(methodsMap.getOrDefault(ref.getSignatureBefore(), new ArrayList<>()));
      Gson gson = new Gson();
      refsBefore.add(gson.toJson(ref));
      methodsMap.put(ref.getSignatureAfter(), refsBefore);
      methodsMap.remove(ref.getSignatureBefore());
    }
  }
}