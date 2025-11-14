package com.droidnotes.kiddolist.feature.taskboard

import com.droidnotes.kiddolist.coredata.Task
import com.droidnotes.kiddolist.coredata.TaskRepository
import InMemoryTaskRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: TaskRepository = HistoryRepoProvider.provide(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    val completed: StateFlow<List<Task>> = repository
        .observeAll()
        .map { list -> list.filter { it.isDone } }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    fun restore(id: Long) {
        scope.launch { repository.toggleDone(id) }
    }

    fun delete(id: Long) {
        scope.launch { repository.delete(id) }
    }

    fun clearAll(ids: List<Long>) {
        if (ids.isEmpty()) return
        scope.launch {
            ids.forEach { repository.delete(it) }
        }
    }
}

// DI helper: resolve TaskRepository from Koin, fallback to in-memory for previews/iOS
private object HistoryRepoProvider : KoinComponent {
    fun provide(): TaskRepository = runCatching { get<TaskRepository>() }.getOrElse { InMemoryTaskRepository() }
}
