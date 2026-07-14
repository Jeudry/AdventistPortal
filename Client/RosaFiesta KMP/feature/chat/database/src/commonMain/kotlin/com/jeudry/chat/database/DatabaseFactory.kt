package com.adventistportal.chat.database

import androidx.room.RoomDatabase

expect class DatabaseFactory {
    fun create(): RoomDatabase.Builder<AdventistPortalChatDatabase>
}