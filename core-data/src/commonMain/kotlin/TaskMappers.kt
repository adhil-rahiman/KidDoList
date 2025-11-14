import com.droidnotes.kiddolist.coredata.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal fun TaskEntity.toDomain(): Task =
    Task(id = id, title = title, content = content, isDone = isDone)

internal fun Task.toEntity(): TaskEntity =
    TaskEntity(id = id, title = title, content = content, isDone = isDone)

internal fun Flow<List<TaskEntity>>.toDomainList(): Flow<List<Task>> =
    this.map { list -> list.map { it.toDomain() } }
