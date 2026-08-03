package com.adventistportal.chat.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.adventistportal.chat.database.dao.ChatDao
import com.adventistportal.chat.database.dao.ChatMessageDao
import com.adventistportal.chat.database.dao.ChatParticipantDao
import com.adventistportal.chat.database.dao.ChatParticipantsCrossRefDao
import com.adventistportal.chat.database.entities.ChatEntity
import com.adventistportal.chat.database.entities.ChatMessageEntity
import com.adventistportal.chat.database.entities.ChatParticipantCrossRef
import com.adventistportal.chat.database.entities.ChatParticipantEntity
import com.adventistportal.chat.database.view.LastMessageView

@Database(
    entities = [
        ChatEntity::class,
        ChatParticipantEntity::class,
        ChatMessageEntity::class,
        ChatParticipantCrossRef::class,
    ],
    views = [
        LastMessageView::class
    ],
    version = 1,
)
@ConstructedBy(AdventistPortalChatDatabaseConstructor::class)
abstract class AdventistPortalChatDatabase: RoomDatabase() {
    abstract val chatDao: ChatDao
    abstract val chatParticipantDao: ChatParticipantDao
    abstract val chatMessageDao: ChatMessageDao
    abstract val chatParticipantsCrossRefDao: ChatParticipantsCrossRefDao

    companion object {
        const val DB_NAME = "adventistportal.db"
    }
}