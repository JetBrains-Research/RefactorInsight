import com.google.gson.Gson;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.annotations.MapAnnotation;
import git4idea.history.GitHistoryUtils;
import git4idea.repo.GitRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

@State(name = "ChangesState",
    storages = {@Storage("refactorings.xml")})
@Service
public class MiningService implements PersistentStateComponent<MiningService.MyState> {

  private boolean loaded = false;
  private MyState innerState = new MyState();

  public MiningService(Project project) {
  }

  public static MiningService getInstance(@NotNull Project project) {
    return ServiceManager.getService(project, MiningService.class);
  }

  @Override
  public MyState getState() {
    return innerState;
  }

  @Override
  public void loadState(MyState state) {
    innerState = state;
  }

  /**
   * Start mining the git repository.
   *
   * @param repository Git repository
   */
  public void mineRepo(GitRepository repository,
                       ConcurrentHashMap<String, List<String>> methodMap) {
    if (!loaded) {
      return;
    }
    List<MethodRefactoring> renameOperations = new ArrayList<>();
    ProgressManager.getInstance()
            .run(new Task.Backgroundable(repository.getProject(), "Mining refactorings") {
              public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setText("Mining refactorings");
                ExecutorService pool = Executors.newFixedThreadPool(8);
                CommitMiner miner =  new CommitMiner(pool, innerState.map, methodMap, repository);
                try {
                  GitHistoryUtils.loadDetails(repository.getProject(), repository.getRoot(),
                          miner, "--all");
                } catch (Exception exception) {
                  exception.printStackTrace();
                }
                pool.shutdown();
                try {
                  pool.awaitTermination(5, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
                renameOperations.addAll(miner.renameOperations);
                processRenameOperations(renameOperations, methodMap);
                System.out.println("done");
                progressIndicator.setText("Finished");
              }
            });
  }

  private void processRenameOperations(List<MethodRefactoring> renameOperations,
                                       ConcurrentHashMap<String, List<String>> methodsMap) {

    renameOperations.sort(new Comparator<>() {
      @Override
      public int compare(MethodRefactoring o1, MethodRefactoring o2) {
        return Long.compare(o1.getData().getTimeOfCommit(), o2.getData().getTimeOfCommit());
      }
    });

    for (MethodRefactoring ref :renameOperations) {
      //get the refactorings before renaming and add into them the new RENAME operation refactoring
      List<String> refsBefore =  methodsMap.getOrDefault(ref.getData()
              .getMethodBefore().getName(), null);
      if (refsBefore != null) {
        Gson gson = new Gson();
        refsBefore.add(gson.toJson(ref));
        methodsMap.put(ref.getData().getMethodAfter().getName(), refsBefore);
        //remove the refactorings for the old method's name since it does not exist anymore
        //methodsMap.remove(ref.getData().getMethodBefore().getName());
      } else {
        Gson gson = new Gson();
        List<String> list = new ArrayList<>();
        list.add(gson.toJson(ref));
        methodsMap.put(ref.getData().getMethodAfter().getName(), list);
      }
    }
  }

  public List<String> getRefactorings(String commitHash) {
    return innerState.map.getOrDefault(commitHash, Arrays.asList("Commit not mined"));
  }

  public void loaded() {
    loaded = true;
  }

  public static class MyState {
    @NotNull
    @MapAnnotation
    public Map<String, List<String>> map = new ConcurrentHashMap<>();
  }


}
