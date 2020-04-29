import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.project.Project

@State(name = "MyService")
class MyService(project: Project?) : PersistentStateComponent<MyService.MyState?> {
    private var innerState = MyState()
    override fun getState(): MyState? {
        return innerState
    }

    override fun loadState(state: MyState) {
        innerState = state
    }

    class MyState internal constructor() {
        var persistentState = "bla"
    }

    companion object {
        fun getInstance(project: Project): MyService {
            return ServiceManager.getService(project, MyService::class.java)
        }
    }
}