package com.droidnotes.kiddolist

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform