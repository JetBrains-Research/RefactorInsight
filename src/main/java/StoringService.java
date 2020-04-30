import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@State(name = "ChangesState",
        storages = {@Storage("refactorings.xml")})
public class StoringService implements PersistentStateComponent<StoringService.MyState> {
    public StoringService(Project project) {
    }

    public static StoringService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, StoringService.class);
    }
    private MyState innerState = new MyState();

    @Override
    public MyState getState() {
        return innerState;
    }

    @Override
    public void loadState(MyState state) {
        innerState = state;
    }

    static class MyState {
        public Map map = new HashMap<String, List<String>>();
    }
}
