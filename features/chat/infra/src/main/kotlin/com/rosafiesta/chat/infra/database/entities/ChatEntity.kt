package com.rosafiesta.chat.infra.database.entities

import com.rosafiesta.core.domain.types.ChatId
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
@Table(
    name = "chats",
    schema = "chat_service"
)
class ChatEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: ChatId? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    var creator: ChatParticipantEntity? = null,
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "chat_participants_cross_ref",
        schema = "chat_service",
        joinColumns = [JoinColumn(name = "chat_id")],
        inverseJoinColumns = [JoinColumn(name = "participant_id")],
        indexes = [
            // Answers efficiently the question: "What are the participants of this chat?"
            Index(
                name = "idx_chat_participant_chat_id_participant_id",
                columnList = "chat_id, participant_id",
                unique = true
            ),
            // Answers efficiently the question: "What chats does this participant belong to?""
            Index(
                name = "idx_chat_participant_participant_id_chat_id",
                columnList = "participant_id, chat_id",
                unique = true
            )
        ]
    )
    var participants: Set<ChatParticipantEntity> = emptySet(),
    @CreationTimestamp
    var createdAt: Instant = Instant.now()
) {

}