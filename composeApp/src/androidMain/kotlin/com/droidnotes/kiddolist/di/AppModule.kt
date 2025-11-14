package com.droidnotes.kiddolist.di

import com.droidnotes.kiddolist.coredata.TaskRepository
import com.droidnotes.kiddolist.corenetwork.TaskSyncService
import com.droidnotes.kiddolist.corenetwork.FakeTaskSyncService
import com.droidnotes.kiddolist.corenetwork.SyncingTaskRepository
import com.droidnotes.kiddolist.feature.taskboard.TaskBoardViewModel
import com.droidnotes.kiddolist.feature.taskboard.HistoryViewModel
import createTaskRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single<TaskSyncService> { FakeTaskSyncService() }

    single<TaskRepository>(qualifier = named("local"), createdAtStart = false) {
        createTaskRepository(androidContext())
    }

    single<TaskRepository> {
        val local = get<TaskRepository>(qualifier = named("local"))
        val network = get<TaskSyncService>()
        SyncingTaskRepository(local, network)
    }

    factory { TaskBoardViewModel(get()) }
    factory { HistoryViewModel(get()) }
}
