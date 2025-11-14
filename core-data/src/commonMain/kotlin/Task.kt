package com.droidnotes.kiddolist.coredata

data class Task(
    val id: Long,
    val title: String,
    val content: String = "",
    val isDone: Boolean = false,
)
