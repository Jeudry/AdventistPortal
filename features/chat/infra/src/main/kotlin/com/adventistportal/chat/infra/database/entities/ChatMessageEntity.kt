package com.adventistportal.chat.infra.database.entities

import com.adventistportal.core.domain.types.ChatId
import com.adventistportal.core.domain.types.ChatMessageId
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.domain.Persistable
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.Instant

@Entity
@Table(
    name = "chat_messages",
    schema = "chat_service",
    indexes = [
        Index(name = "idx_chat_message_chat_id_created_at",
            columnList = "chat_id,created_at DESC"
        ),
    ]
)
/**
 * The id is assigned by the application, not generated: a client may send one so that a
 * message retried over a flaky connection lands once rather than twice.
 *
 * That makes [Persistable] necessary. Spring Data decides between persist and merge by
 * whether the id is set, so an assigned id looks detached — and merging a row that does
 * not exist yet fails with StaleObjectStateException, which is how this was discovered.
 */
class ChatMessageEntity(
    @Id
    private var id: ChatMessageId,
    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,
    @Column(name = "chat_id", nullable = false, updatable = false)
    var chatId: ChatId? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "chat_id",
        nullable = false,
        insertable = false,
        updatable = false,
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var chat: ChatEntity? = null,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
        name = "sender_id",
        nullable = false,
    )
    var sender: ChatParticipantEntity? = null,
    @CreationTimestamp
    @Column(nullable = false)
    var createdAt: Instant = Instant.now(),

    /** Set by whoever constructs it; Hibernate reads rows back through the no-arg path. */
    @Transient
    private var isNew: Boolean = true,
) : Persistable<ChatMessageId> {

    override fun getId(): ChatMessageId = id

    override fun isNew(): Boolean = isNew

    @PostPersist
    @PostLoad
    fun settle() {
        isNew = false
    }
}