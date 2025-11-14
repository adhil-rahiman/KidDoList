import com.droidnotes.kiddolist.coredata.Task
import com.droidnotes.kiddolist.coredata.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryTaskRepository : TaskRepository {
    private val tasks = MutableStateFlow<List<Task>>(emptyList())
    private var nextId = 1L

    override fun observeAll(): Flow<List<Task>> = tasks.asStateFlow()

    override suspend fun add(title: String, content: String): Task {
        val task = Task(id = nextId++, title = title, content = content, isDone = false)
        tasks.value = tasks.value + task
        return task
    }

    override suspend fun get(id: Long): Task? {
        return tasks.value.firstOrNull { it.id == id }
    }

    override suspend fun update(id: Long, title: String, content: String) {
        tasks.value = tasks.value.map { t -> if (t.id == id) t.copy(title = title, content = content) else t }
    }

    override suspend fun toggleDone(id: Long) {
        tasks.value = tasks.value.map { t -> if (t.id == id) t.copy(isDone = !t.isDone) else t }
    }

    override suspend fun delete(id: Long) {
        tasks.value = tasks.value.filterNot { it.id == id }
    }
}
