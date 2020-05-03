import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.annotations.MapAnnotation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@State(name = "ChangesState",
        storages = {@Storage("refactorings.xml")})
@Service
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


    public static class MyState {
        @NotNull
        @MapAnnotation
        public Map<String, List<String>> map;
        MyState() {
            map = new HashMap<String, List<String>>();
        }
    }
}
