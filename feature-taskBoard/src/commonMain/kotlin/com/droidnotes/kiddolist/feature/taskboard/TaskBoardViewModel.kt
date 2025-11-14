package com.droidnotes.kiddolist.feature.taskboard

import com.droidnotes.kiddolist.coredata.Task
import com.droidnotes.kiddolist.coredata.TaskRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import InMemoryTaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.emptyList
import kotlin.text.isBlank

class TaskBoardViewModel(
    private val repository: TaskRepository = TaskRepoProvider.provide(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    val tasks: StateFlow<List<Task>> = repository
        .observeAll()
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    fun addTask(title: String) {
        if (title.isBlank()) return
        scope.launch { repository.add(title) }
    }

    fun addTask(title: String, content: String) {
        if (title.isBlank()) return
        scope.launch { repository.add(title, content) }
    }

    fun toggleDone(id: Long) {
        scope.launch { repository.toggleDone(id) }
    }

    fun delete(id: Long) {
        scope.launch { repository.delete(id) }
    }
}

// DI helper: resolve TaskRepository from Koin, fallback to in-memory for previews/IOS without DI
private object TaskRepoProvider : KoinComponent {
    fun provide(): TaskRepository = runCatching { get<TaskRepository>() }.getOrElse { InMemoryTaskRepository() }
}
