import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.droidnotes.kiddolist.coredata.TaskRepository
import RoomTaskRepository


internal fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
  val appContext = context.applicationContext
  val dbFile = appContext.getDatabasePath("my_room.db")
  return Room.databaseBuilder<AppDatabase>(
    context = appContext,
    name = dbFile.absolutePath
  )
}

// Convenience API to avoid exposing Room types to Android app modules.
internal fun getDatabase(context: Context): AppDatabase {
  return getRoomDatabase(getDatabaseBuilder(context))
}

// High-level factory to avoid exposing Room and AppDatabase to app module
fun createTaskRepository(context: Context): TaskRepository {
  val db = getDatabase(context)
  return RoomTaskRepository(db.getDao())
}