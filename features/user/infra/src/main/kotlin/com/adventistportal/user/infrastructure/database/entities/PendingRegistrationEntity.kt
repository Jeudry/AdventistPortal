package com.adventistportal.user.infrastructure.database.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.UUID

/**
 * A registration in progress. The account only becomes a row in `users` once the e-mail
 * is confirmed and the remaining details are given, which is what lets every column in
 * `users` be NOT NULL. Rows here expire and are swept.
 */
@Entity
@Table(name = "pending_registrations", schema = "user_service")
class PendingRegistrationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false, unique = true)
    var username: String,

    @Column(nullable = false)
    var hashedPassword: String,

    /** Set when the e-mail is confirmed. Until then the registration cannot be completed. */
    @Column
    var verifiedAt: Instant? = null,

    @Column(nullable = false)
    var expiresAt: Instant,

    @CreationTimestamp
    @Column(nullable = false)
    var createdAt: Instant = Instant.now(),
) {
    val isVerified: Boolean get() = verifiedAt != null
    val isExpired: Boolean get() = Instant.now().isAfter(expiresAt)
}
