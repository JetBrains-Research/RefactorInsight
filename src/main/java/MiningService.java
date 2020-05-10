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
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
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
            CommitMiner miner = new CommitMiner(pool, innerState.map, methodMap, repository);
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
            changeMethodsNames(miner.classRenames, methodMap);
            System.out.println("done");
            progressIndicator.setText("Finished");
          }
        });
  }

  private void changeMethodsNames(List<ClassRename> classRenames,
                                  ConcurrentHashMap<String, List<String>> methodsMap) {
    classRenames.sort(new Comparator<ClassRename>() {
      @Override
      public int compare(ClassRename o1, ClassRename o2) {
        return Long.compare(o1.getCommitTime(), o2.getCommitTime());
      }
    });

    List<String> keys = new ArrayList<>();
    Enumeration<String> ks = methodsMap.keys();
    while (ks.hasMoreElements()) {
      keys.add(ks.nextElement());
    }

    for (ClassRename classRename : classRenames) {
      List<String> methods = keys.stream()
          .filter(x -> x.substring(0, x.lastIndexOf("."))
              .equals(classRename.getClassBefore())).collect(Collectors.toList());
      for (String m : methods) {
        List<String> refs = new ArrayList<>();
        refs.addAll(methodsMap.getOrDefault(m, new ArrayList<>()));
        String newKey = classRename.getClassAfter() + m.substring(m.lastIndexOf("."));
        methodsMap.put(newKey, refs);
        //methodsMap.remove(m);
      }
    }
  }

  private void processRenameOperations(List<MethodRefactoring> renameOperations,
                                       ConcurrentHashMap<String, List<String>> methodsMap) {

    renameOperations.sort(new Comparator<>() {
      @Override
      public int compare(MethodRefactoring o1, MethodRefactoring o2) {
        return Long.compare(o1.getData().getTimeOfCommit(), o2.getData().getTimeOfCommit());
      }
    });

    for (MethodRefactoring ref : renameOperations) {
      //get the refactorings before renaming and add into them the new RENAME operation refactoring
      List<String> refsBefore = new ArrayList<>();
      refsBefore
          .addAll(methodsMap.getOrDefault(ref.getData().getMethodBefore(), new ArrayList<>()));

      Gson gson = new Gson();
      refsBefore.add(gson.toJson(ref));
      methodsMap.put(ref.getData().getMethodAfter(), refsBefore);
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
