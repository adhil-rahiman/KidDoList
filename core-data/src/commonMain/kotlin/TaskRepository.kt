package com.droidnotes.kiddolist.coredata

import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeAll(): Flow<List<Task>>
    suspend fun add(title: String, content: String = ""): Task
    suspend fun get(id: Long): Task?
    suspend fun update(id: Long, title: String, content: String)
    suspend fun toggleDone(id: Long)
    suspend fun delete(id: Long)
}
