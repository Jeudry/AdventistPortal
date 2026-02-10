package com.rosafiesta.chat.infra.database.entities

import com.rosafiesta.core.domain.types.UserId
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
@Table(
    name = "chat_participants",
    schema = "chat_service",
    indexes = [
        Index(name = "idx_chat_participant_username", columnList = "username", unique = true),
        Index(name = "idx_chat_participant_email", columnList = "email", unique = true),
    ]
)
class ChatParticipantEntity(
    @Id
    var userId: UserId? = null,
    @Column(nullable = false, unique = true)
    var username: String = "",
    @Column(nullable = false, unique = true)
    var email: String = "",
    @Column(nullable = true, columnDefinition = "TEXT")
    var profilePictureUrl: String? = null,
    @CreationTimestamp
    var createdAt: Instant = Instant.now()
)