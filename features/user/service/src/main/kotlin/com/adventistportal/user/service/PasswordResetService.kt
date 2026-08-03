package com.adventistportal.user.service

import com.adventistportal.core.domain.exceptions.InvalidTokenEx
import com.adventistportal.core.domain.events.user.UserEvent
import com.adventistportal.user.domain.exception.InvalidCredentialsEx
import com.adventistportal.user.domain.exception.PasswordHashFailedEx
import com.adventistportal.user.domain.exception.SamePasswordEx
import com.adventistportal.user.domain.exception.UserNotFoundEx
import com.adventistportal.core.domain.types.UserId
import com.adventistportal.user.infrastructure.database.entities.PasswordResetTokenEntity
import com.adventistportal.user.infrastructure.database.repositories.PasswordResetTokenRepository
import com.adventistportal.user.infrastructure.database.repositories.RefreshTokenRepository
import com.adventistportal.user.infrastructure.database.repositories.UserRepository
import com.adventistportal.core.infrastructure.message_queue.EventPublisher
import com.adventistportal.user.infrastructure.security.PasswordEncoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant.now
import java.time.temporal.ChronoUnit

@Service
class PasswordResetService(
    private val userRepository: UserRepository,
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    @param:Value("\${adventistportal.email.reset-password.expiry-minutes}") private val expiryMinutes: Long,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val eventPublisher: EventPublisher
){
    @Transactional
    fun requestPasswordReset(email: String) {
        val user = userRepository.findByEmail(email) ?: return

        passwordResetTokenRepository.invalidateActiveTokensForUser(user)

        val token = PasswordResetTokenEntity(
            user = user,
            expiresAt = now().plus(expiryMinutes, ChronoUnit.MINUTES)
        )

        passwordResetTokenRepository.save(token)

        eventPublisher.publish(
            UserEvent.RequestResetPassword(
                userId = user.id!!,
                email = user.email,
                username = user.username,
                verificationToken = token.token,
                expiresInMinutes = expiryMinutes
            )
        )
    }

    @Transactional
    fun resetPassword(token: String, password: String) {
        val passwordResetToken = passwordResetTokenRepository.findByToken(token)
            ?: throw InvalidTokenEx("Invalid password reset token")

        if(passwordResetToken.isUsed){
            throw InvalidTokenEx("The token has already been used.")
        }

        if(passwordResetToken.isExpired){
            throw InvalidTokenEx("The token has expired.")
        }

        val user = passwordResetToken.user

        val matches = passwordEncoder.matches(password, user.hashedPassword)

        if(matches){
            throw SamePasswordEx()
        }

        val hashedNewPassword = passwordEncoder.encode(password) ?: throw PasswordHashFailedEx()

        userRepository.save(user.apply {
            this.hashedPassword = hashedNewPassword
        })

        passwordResetTokenRepository.save(passwordResetToken.apply {
            this.usedAt = now()
        })

        refreshTokenRepository.deleteByUserId(user.id!!)
    }

    @Transactional
    fun changePassword(
        userId: UserId,
        oldPassword: String,
        newPassword: String
    ){
        val user = userRepository.findById(userId).orElseThrow {
            throw UserNotFoundEx()
        }

        if(!passwordEncoder.matches(oldPassword, user.hashedPassword)){
            throw InvalidCredentialsEx()
        }

        if(oldPassword == newPassword){
            throw SamePasswordEx()
        }

        refreshTokenRepository.deleteByUserId(user.id!!)

        val newHashedPassword = passwordEncoder.encode(newPassword)
            ?: throw PasswordHashFailedEx()

        userRepository.save(user.apply {
            hashedPassword = newHashedPassword
        })
    }

    @Scheduled(cron = "0 0 3 * * *")
    fun cleanUpExpiredPasswords(){
        passwordResetTokenRepository.deleteByExpiresAtLessThan(
            now = now()
        )
    }
}