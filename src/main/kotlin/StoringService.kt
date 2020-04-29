import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.project.Project

@State(name = "StoringService")
class StoringService(project: Project?) : PersistentStateComponent<StoringService.MyState?> {
    private var innerState = MyState()
    override fun getState(): MyState? {
        return innerState
    }

    override fun loadState(state: MyState) {
        innerState = state
    }

    class MyState internal constructor() {
        var map = HashMap<String, List<String>>()
    }

    companion object {
        fun getInstance(project: Project): StoringService {
            return ServiceManager.getService(project, StoringService::class.java)
        }
    }
}