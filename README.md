KidDoList – Kotlin Multiplatform (Android + iOS)

Demo
https://github.com/user-attachments/assets/61f93771-5dc1-4473-8fa4-d20e2d732a34



Modules
- composeApp: Application entry and navigation (Compose Multiplatform UI).
- feature-taskBoard: Task List, Add/Edit, and History screens with ViewModels.
- core-data: Room MPP database, DAO, entities, and TaskRepository (local).
- core-network: Dummy sync layer (FakeTaskSyncService) and SyncingTaskRepository.
- shared: Small shared utilities/constants.
- iosApp: iOS host app (Xcode project).

Build & Run
- Android: ./gradlew :composeApp:installDebug (or run from IDE)
- iOS: Open iosApp/ in Xcode and run the iOS scheme - Not tested yet

Tech stack 
- UI: Compose Multiplatform + Material3 + Navigation
- DI: Koin (initialized in Android MainActivity)
- Data: Room (multiplatform driver), Flow/Coroutines
- Sync: core-network fake service with background push/pull
