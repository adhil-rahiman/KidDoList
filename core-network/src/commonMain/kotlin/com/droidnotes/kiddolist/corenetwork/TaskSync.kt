package com.droidnotes.kiddolist.corenetwork

import com.droidnotes.kiddolist.coredata.Task
import kotlinx.coroutines.delay
import kotlin.collections.linkedMapOf
import kotlin.io.println

data class NetworkTask(
    val id: Long,
    val title: String,
    val content: String,
    val isDone: Boolean,
)

interface TaskSyncService {
    suspend fun upsert(task: NetworkTask)
    suspend fun delete(id: Long)
    suspend fun fetchAll(): List<NetworkTask>
}

fun Task.toNetwork(): NetworkTask = NetworkTask(id, title, content, isDone)


class FakeTaskSyncService : TaskSyncService {
    private val remote = linkedMapOf<Long, NetworkTask>()

    override suspend fun upsert(task: NetworkTask) {
        delay(80)
        remote[task.id] = task
        println("[SYNC] upsert remote id=${task.id}")
    }

    override suspend fun delete(id: Long) {
        delay(60)
        remote.remove(id)
        println("[SYNC] delete remote id=$id")
    }

    override suspend fun fetchAll(): List<NetworkTask> {
        delay(100)
        return remote.values.toList()
    }
}
