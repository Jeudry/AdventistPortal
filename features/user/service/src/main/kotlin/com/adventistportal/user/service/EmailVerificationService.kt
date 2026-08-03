package com.adventistportal.user.service

import com.adventistportal.core.domain.exceptions.InvalidTokenEx
import com.adventistportal.core.domain.events.user.UserEvent
import com.adventistportal.user.domain.exception.UserNotFoundEx
import com.adventistportal.user.domain.model.EmailVerificationToken
import com.adventistportal.user.infrastructure.database.entities.EmailVerificationTokenEntity
import com.adventistportal.user.infrastructure.database.mappers.toModel
import com.adventistportal.user.infrastructure.database.repositories.EmailVerificationTokenRepository
import com.adventistportal.user.infrastructure.database.repositories.UserRepository
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
    private val userRepository: UserRepository,
    @param:Value("\${adventistportal.email.verification.expiry-hours}") private val expiryHours: Long,
    private val eventPublisher: EventPublisher
){

    @Transactional
    fun resendVerificationEmail(email: String) {
        val token = createVerificationToken(email)
        if(token.user.hasEmailVerified){
            return
        }

        eventPublisher.publish(
            event = UserEvent.RequestResendVerification(
                userId = token.user.id,
                email = token.user.email,
                username = token.user.username,
                verificationToken = token.token
            )
        )
    }

    @Transactional
    fun createVerificationToken(email: String): EmailVerificationToken {
        val userEntity = userRepository.findByEmail(email) ?: throw UserNotFoundEx()

        emailVerificationTokenRepository.invalidateActiveTokensForUser(userEntity)

        val token = EmailVerificationTokenEntity(
            expiresAt = now().plus(expiryHours, ChronoUnit.HOURS),
            user = userEntity,
        )

        return emailVerificationTokenRepository.save(token).toModel()
    }

    fun verifyEmail(token: String){
        val verificationToken = emailVerificationTokenRepository.findByToken(token) ?: throw UserNotFoundEx()

        if(verificationToken.isUsed){
            throw InvalidTokenEx("The token has already been used.")
        }

        if(verificationToken.isExpired){
            throw InvalidTokenEx("The token has expired.")
        }

        emailVerificationTokenRepository.save(verificationToken.apply {
            this.usedAt = now()
        })

        val user = userRepository.save(
            verificationToken.user.apply {
                hasVerifiedEmail = true
            }
        ).toModel()

        eventPublisher.publish(
            UserEvent.Verified(
                userId = verificationToken.user.id!!,
                email = verificationToken.user.email,
                username = verificationToken.user.username
            )
        )
    }

    @Scheduled(cron = "0 0 3 * * *")
    fun cleanupExpiredTokens(){
        emailVerificationTokenRepository.deleteByExpiresAtLessThan(now = now())
    }
}