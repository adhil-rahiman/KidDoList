package com.droidnotes.kiddolist.corenetwork

import com.droidnotes.kiddolist.coredata.Task
import com.droidnotes.kiddolist.coredata.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.emptyList
import kotlin.collections.forEach
import kotlin.io.println
import kotlin.let

class SyncingTaskRepository(
    private val delegate: TaskRepository,
    private val network: TaskSyncService,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) : TaskRepository {

    override fun observeAll() = delegate.observeAll()

    init {
        delegate.observeAll()
            .debounce(400)
            .distinctUntilChanged()
            .onEach { list ->
                scope.launch {
                    list.forEach { task ->
                        try {
                            network.upsert(task.toNetwork())
                        } catch (t: Throwable) {
                            println("[SYNC] snapshot upsert failed for id=${task.id}: ${t.message}")
                        }
                    }
                }
            }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

        scope.launch {
            while (true) {
                try {
                    val remote = network.fetchAll()
                    remote.forEach { net ->
                        val local = delegate.get(net.id)
                        if (local != null) {
                            if (local.title != net.title || local.content != net.content) {
                                delegate.update(net.id, net.title, net.content)
                            }
                            if (local.isDone != net.isDone) {
                                delegate.toggleDone(net.id)
                            }
                        }
                    }
                } catch (t: Throwable) {
                    println("[SYNC] pull failed: ${t.message}")
                }
                delay(5_000)
            }
        }
    }

    override suspend fun add(title: String, content: String): Task {
        val added = delegate.add(title, content)
        try { network.upsert(added.toNetwork()) } catch (_: Throwable) {}
        return added
    }

    override suspend fun get(id: Long): Task? = delegate.get(id)

    override suspend fun update(id: Long, title: String, content: String) {
        delegate.update(id, title, content)
        delegate.get(id)?.let { task ->
            try { network.upsert(task.toNetwork()) } catch (_: Throwable) {}
        }
    }

    override suspend fun toggleDone(id: Long) {
        delegate.toggleDone(id)
        delegate.get(id)?.let { task ->
            try { network.upsert(task.toNetwork()) } catch (_: Throwable) {}
        }
    }

    override suspend fun delete(id: Long) {
        delegate.delete(id)
        try { network.delete(id) } catch (_: Throwable) {}
    }
}
