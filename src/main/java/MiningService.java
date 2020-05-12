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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

@State(name = "ChangesState",
    storages = {@Storage("refactorings.xml")})
@Service
public class MiningService implements PersistentStateComponent<MiningService.MyState> {

  private boolean loaded = false;
  private boolean first = true;
  private boolean mining = false;
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
   * Mine compelete git repo for refactorings.
   *
   * @param repository GitRepository
   */
  public void mineRepo(GitRepository repository,
                       ConcurrentHashMap<String, List<String>> methodsMap) {
    if (!loaded) {
      return;
    }

    int limit = 100;
    try {
      limit = getCommitCount(repository);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      mineRepo(repository, methodsMap, limit);
    }

  }

  /**
   * Mine repo with limit.
   *
   * @param repository GitRepository
   * @param limit      int
   */
  public void mineRepo(GitRepository repository,
                       ConcurrentHashMap<String, List<String>> methodsMap, int limit) {
    if (!loaded) {
      return;
    }
    ProgressManager.getInstance()
        .run(new Task.Backgroundable(repository.getProject(), "Mining refactorings") {
          public void run(@NotNull ProgressIndicator progressIndicator) {
            mining = true;
            progressIndicator.setText("Mining refactorings");
            progressIndicator.setIndeterminate(false);
            int cores = 8; //Runtime.getRuntime().availableProcessors();
            ExecutorService pool = Executors.newFixedThreadPool(cores);
            System.out.println("Mining started on " + cores + " cores");
            AtomicInteger commitsDone = new AtomicInteger(0);
            CommitMiner miner =
                new CommitMiner(pool, innerState.map, methodsMap, repository, commitsDone,
                    progressIndicator,
                    limit);
            try {
              String logArgs = "--max-count=" + limit;
              GitHistoryUtils.loadDetails(repository.getProject(), repository.getRoot(),
                  miner, logArgs);
            } catch (Exception exception) {
              exception.printStackTrace();
            } finally {
              mining = false;
            }
            pool.shutdown();

            try {
              pool.awaitTermination(5, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            List<MethodRefactoring> renameOperations = new ArrayList<>();
            renameOperations.addAll(miner.renameOperations);
            processRenameOperations(renameOperations, methodsMap);
            changeMethodsNames(miner.classRenames, methodsMap);
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
    return innerState.map.getOrDefault(commitHash, Arrays.asList(""));
  }

  public void loaded() {
    loaded = true;
  }

  /**
   * Get the total ammount of commits in a repository.
   *
   * @param repository GitRepository
   * @return int, ammount of commits
   * @throws IOException bla
   */
  public int getCommitCount(GitRepository repository) throws IOException {
    Process process = Runtime.getRuntime().exec("git rev-list --all --count", null,
        new File(repository.getRoot().getCanonicalPath()));
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    String output = reader.readLine();
    System.out.println(output);
    return Integer.parseInt(output);
  }

  public static class MyState {
    @NotNull
    @MapAnnotation
    public Map<String, List<String>> map = new ConcurrentHashMap<>();
  }

}
