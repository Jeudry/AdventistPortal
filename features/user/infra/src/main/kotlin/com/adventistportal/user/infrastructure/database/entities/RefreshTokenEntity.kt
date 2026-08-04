package com.adventistportal.user.infrastructure.database.entities

import com.adventistportal.core.domain.types.UserId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(
    name = "refresh_tokens", schema = "user_service",
    indexes = [
        Index(name = "idx_refresh_tokens_user_id", columnList = "user_id"),
        Index(name = "idx_refresh_tokens_user_token", columnList = "user_id, hashed_token")
    ]
)
class RefreshTokenEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    val userId: UserId,
    @Column(nullable = false)
    val hashedToken: String,
    @CreationTimestamp
    @Column(nullable = false)
    var createdAt: Instant = Instant.now(),
    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: Instant = Instant.now(),
)