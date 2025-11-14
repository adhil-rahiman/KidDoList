import com.droidnotes.kiddolist.coredata.Task
import com.droidnotes.kiddolist.coredata.TaskRepository
import kotlinx.coroutines.flow.Flow

class RoomTaskRepository(private val dao: TaskDao) : TaskRepository {
    override fun observeAll(): Flow<List<Task>> = dao.getAllAsFlow().toDomainList()

    override suspend fun add(title: String, content: String): Task {
        val id = dao.insert(TaskEntity(title = title, content = content))
        return Task(id = id, title = title, content = content, isDone = false)
    }

    override suspend fun get(id: Long): Task? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun update(id: Long, title: String, content: String) {
        dao.update(id, title, content)
    }

    override suspend fun toggleDone(id: Long) {
        dao.toggleDone(id)
    }

    override suspend fun delete(id: Long) {
        dao.delete(id)
    }
}
