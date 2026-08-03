package com.adventistportal.chat.database

import androidx.room.Room
import androidx.room.RoomDatabase
import com.adventistportal.core.data.util.appDataDirectory
import java.io.File

actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<AdventistPortalChatDatabase> {
        val directory = appDataDirectory

        if(!directory.exists()) {
            directory.mkdirs()
        }

        val dbFile = File(directory, AdventistPortalChatDatabase.DB_NAME)
        return Room.databaseBuilder(dbFile.absolutePath)
    }
}