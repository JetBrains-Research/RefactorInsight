import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.annotations.MapAnnotation;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "MethodsState",
    storages = {@Storage("refactoringsMethods.xml")})
@Service
public class MethodService implements PersistentStateComponent<MethodService.MyState> {

  private MyState innerState = new MyState();

  @Nullable
  @Override
  public MyState getState() {
    return innerState;
  }

  @Override
  public void loadState(@NotNull MyState state) {
    this.innerState = state;
  }

  public static class MyState {

    @NotNull
    @MapAnnotation
    public ConcurrentHashMap<String, List<String>> map = new ConcurrentHashMap<>();
  }
}
