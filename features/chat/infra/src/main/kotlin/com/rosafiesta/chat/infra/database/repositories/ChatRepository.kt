package com.adventistportal.chat.infra.database.repositories

import com.adventistportal.core.domain.types.ChatId
import com.adventistportal.core.domain.types.UserId
import com.adventistportal.chat.infra.database.entities.ChatEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ChatRepository: JpaRepository<ChatEntity, ChatId> {
    // Query a chat by id where the user is a participant
    @Query("""
        SELECT c
        FROM ChatEntity c 
        LEFT JOIN FETCH c.participants
        LEFT JOIN FETCH c.creator
        WHERE c.id = :id
        AND EXISTS (
            SELECT 1
             FROM c.participants p 
             WHERE p.userId = :userId
        )
    """)
    fun findChatById(id: ChatId, userId: UserId): ChatEntity?

    // Query all chats where the user is a participant
    @Query("""
        SELECT c
        FROM ChatEntity c 
        LEFT JOIN FETCH c.participants
        LEFT JOIN FETCH c.creator
        WHERE EXISTS (
            SELECT 1
            FROM c.participants p 
            WHERE p.userId = :userId
        )
    """)
    fun findAllByUserId(userId: UserId): List<ChatEntity>
}