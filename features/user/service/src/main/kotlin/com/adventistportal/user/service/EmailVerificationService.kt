package com.adventistportal.user.service

import com.adventistportal.core.domain.exceptions.InvalidTokenEx
import com.adventistportal.core.domain.events.user.UserEvent
import com.adventistportal.user.domain.exception.UserNotFoundEx
import com.adventistportal.user.domain.model.EmailVerificationToken
import com.adventistportal.user.infrastructure.database.entities.EmailVerificationTokenEntity
import com.adventistportal.user.infrastructure.database.entities.PendingRegistrationEntity
import com.adventistportal.user.infrastructure.database.mappers.toModel
import com.adventistportal.user.infrastructure.database.repositories.EmailVerificationTokenRepository
import com.adventistportal.user.infrastructure.database.repositories.PendingRegistrationRepository
import com.adventistportal.core.infrastructure.message_queue.EventPublisher
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant.now
import java.time.temporal.ChronoUnit

@Service
class EmailVerificationService (
    private val emailVerificationTokenRepository: EmailVerificationTokenRepository,
    private val pendingRegistrationRepository: PendingRegistrationRepository,
    @param:Value("\${adventistportal.email.verification.expiry-hours}") private val expiryHours: Long,
    private val eventPublisher: EventPublisher
){

    @Transactional
    fun resendVerificationEmail(email: String) {
        val token = createVerificationToken(email)
        if (token.isVerified) {
            return
        }

        eventPublisher.publish(
            event = UserEvent.RequestResendVerification(
                registrationId = token.registrationId,
                email = token.email,
                username = token.username,
                verificationToken = token.token
            )
        )
    }

    @Transactional
    fun createVerificationToken(email: String): EmailVerificationToken {
        val registration = pendingRegistrationRepository.findByEmail(email) ?: throw UserNotFoundEx()

        emailVerificationTokenRepository.invalidateActiveTokensFor(registration)

        val token = EmailVerificationTokenEntity(
            expiresAt = now().plus(expiryHours, ChronoUnit.HOURS),
            pendingRegistration = registration,
        )

        return emailVerificationTokenRepository.save(token).toModel()
    }

    /**
     * Confirms the e-mail only. The token stays valid so it can carry the second step —
     * the account is not created until the remaining details arrive.
     */
    @Transactional
    fun verifyEmail(token: String) {
        val verificationToken = requireUsableToken(token)

        val registration = verificationToken.pendingRegistration
        pendingRegistrationRepository.save(registration.apply { this.verifiedAt = now() })

        eventPublisher.publish(
            UserEvent.Verified(
                registrationId = registration.id!!,
                email = registration.email,
                username = registration.username
            )
        )
    }

    fun requireUsableToken(token: String): EmailVerificationTokenEntity {
        val verificationToken = emailVerificationTokenRepository.findByToken(token)
            ?: throw UserNotFoundEx()

        if (verificationToken.isUsed) {
            throw InvalidTokenEx("The token has already been used.")
        }

        if (verificationToken.isExpired) {
            throw InvalidTokenEx("The token has expired.")
        }

        return verificationToken
    }

    /** The tokens go with the registration they belong to; the account now lives in `users`. */
    fun discardTokensFor(registration: PendingRegistrationEntity) {
        emailVerificationTokenRepository.deleteByPendingRegistration(registration)
    }

    @Scheduled(cron = "0 0 3 * * *")
    fun cleanupExpiredTokens(){
        emailVerificationTokenRepository.deleteByExpiresAtLessThan(now = now())
        pendingRegistrationRepository.deleteByExpiresAtLessThan(now = now())
    }
}
