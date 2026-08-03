package com.adventistportal.chat.infra.database.repositories

import com.adventistportal.core.domain.types.ChatId
import com.adventistportal.core.domain.types.ChatMessageId
import com.adventistportal.chat.infra.database.entities.ChatMessageEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface ChatMessageRepository: JpaRepository<ChatMessageEntity, ChatMessageId> {
    @Query("""
        SELECT m
        FROM ChatMessageEntity m 
        WHERE m.chatId = :chatId 
        AND m.createdAt < :before
        ORDER BY m.createdAt DESC
    """)
    fun findByChatIdBefore(
        chatId: ChatId,
        before: Instant,
        pageable: Pageable
    ): Slice<ChatMessageEntity>

    @Query("""
        SELECT m
        FROM ChatMessageEntity m
        LEFT JOIN FETCH m.sender
        WHERE m.chatId IN (:chatIds)
        AND(m.createdAt, m.id) = (
            SELECT m2.createdAt, m2.id
            FROM ChatMessageEntity m2
            WHERE m2.chatId = m.chatId
            ORDER BY m2.createdAt DESC
            LIMIT 1
        )
    """)
    fun findLatestMessagesByChatIds(
        chatIds: Set<ChatId>,
    ): List<ChatMessageEntity>
}