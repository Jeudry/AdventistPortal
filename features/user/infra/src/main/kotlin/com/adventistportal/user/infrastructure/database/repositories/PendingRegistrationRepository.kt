package com.adventistportal.user.infrastructure.database.repositories

import com.adventistportal.user.infrastructure.database.entities.PendingRegistrationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
interface PendingRegistrationRepository : JpaRepository<PendingRegistrationEntity, UUID> {
    fun findByEmail(email: String): PendingRegistrationEntity?
    fun findByEmailOrUsername(email: String, username: String): PendingRegistrationEntity?
    fun deleteByExpiresAtLessThan(now: Instant)
}
