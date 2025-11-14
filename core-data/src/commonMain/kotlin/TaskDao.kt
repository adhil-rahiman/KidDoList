import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
  @Insert
  suspend fun insert(item: TaskEntity): Long

  @Query("SELECT count(*) FROM TaskEntity")
  suspend fun count(): Int

  @Query("SELECT * FROM TaskEntity ORDER BY id DESC")
  fun getAllAsFlow(): Flow<List<TaskEntity>>

  @Query("SELECT * FROM TaskEntity WHERE id = :id LIMIT 1")
  suspend fun getById(id: Long): TaskEntity?

  @Query("UPDATE TaskEntity SET title = :title, content = :content WHERE id = :id")
  suspend fun update(id: Long, title: String, content: String)

  @Query("UPDATE TaskEntity SET isDone = NOT isDone WHERE id = :id")
  suspend fun toggleDone(id: Long)

  @Query("DELETE FROM TaskEntity WHERE id = :id")
  suspend fun delete(id: Long)
}