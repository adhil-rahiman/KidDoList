import androidx.room.RoomDatabaseConstructor

actual object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase {
        error("Room database initialization not set up yet for JVM.")
    }
}
