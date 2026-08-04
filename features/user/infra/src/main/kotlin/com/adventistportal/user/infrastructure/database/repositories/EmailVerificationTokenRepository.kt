package com.adventistportal.user.infrastructure.database.repositories

import com.adventistportal.user.infrastructure.database.entities.EmailVerificationTokenEntity
import com.adventistportal.user.infrastructure.database.entities.PendingRegistrationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface EmailVerificationTokenRepository: JpaRepository<EmailVerificationTokenEntity, Long>{
    fun findByToken(token: String): EmailVerificationTokenEntity?
    fun deleteByExpiresAtLessThan(now: Instant)
    fun deleteByPendingRegistration(registration: PendingRegistrationEntity)
    @Modifying
    @Query("UPDATE EmailVerificationTokenEntity t SET t.usedAt = CURRENT_TIMESTAMP WHERE t.pendingRegistration = :registration")
    fun invalidateActiveTokensFor(registration: PendingRegistrationEntity)
}