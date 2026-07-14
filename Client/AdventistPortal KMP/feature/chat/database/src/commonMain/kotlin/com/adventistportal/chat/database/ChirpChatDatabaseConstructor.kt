package com.adventistportal.chat.database

import androidx.room.RoomDatabaseConstructor

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AdventistPortalChatDatabaseConstructor: RoomDatabaseConstructor<AdventistPortalChatDatabase> {
    override fun initialize(): AdventistPortalChatDatabase
}