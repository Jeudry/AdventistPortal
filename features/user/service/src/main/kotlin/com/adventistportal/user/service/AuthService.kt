package com.adventistportal.user.service

import com.adventistportal.core.domain.exceptions.InvalidTokenEx
import com.adventistportal.core.domain.events.user.UserEvent
import com.adventistportal.user.domain.exception.EmailNotVerifiedEx
import com.adventistportal.user.domain.exception.InvalidCredentialsEx
import com.adventistportal.user.domain.exception.PasswordHashFailedEx
import com.adventistportal.user.domain.exception.UserAlreadyExistsEx
import com.adventistportal.user.domain.exception.UserNotFoundEx
import com.adventistportal.user.domain.model.AuthenticatedUser
import com.adventistportal.user.domain.model.User
import com.adventistportal.core.domain.types.UserId
import com.adventistportal.user.infrastructure.database.entities.RefreshTokenEntity
import com.adventistportal.user.infrastructure.database.entities.UserEntity
import com.adventistportal.user.infrastructure.database.mappers.toModel
import com.adventistportal.user.infrastructure.database.repositories.RefreshTokenRepository
import com.adventistportal.user.infrastructure.database.entities.PendingRegistrationEntity
import com.adventistportal.user.domain.model.PendingRegistration
import com.adventistportal.user.infrastructure.database.repositories.PendingRegistrationRepository
import com.adventistportal.user.infrastructure.database.repositories.UserRepository
import com.adventistportal.core.infrastructure.message_queue.EventPublisher
import com.adventistportal.user.infrastructure.security.PasswordEncoder
import com.adventistportal.core.services.JwtService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Base64

@Service
class AuthService (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val emailVerificationService: EmailVerificationService,
    private val pendingRegistrationRepository: PendingRegistrationRepository,
    private val eventPublisher: EventPublisher
){
    private val registrationExpiryDays = 7L

    /**
     * Starts a registration. No user is created yet — the account only reaches `users`
     * once the e-mail is confirmed and completeRegistration supplies the rest.
     */
    @Transactional
    fun register(username: String, email: String, password: String): PendingRegistration {
        val trimmedEmail = email.trim()
        val trimmedUsername = username.trim()

        if (userRepository.findByEmailOrUsername(trimmedEmail, trimmedUsername) != null) {
            throw UserAlreadyExistsEx()
        }

        pendingRegistrationRepository.findByEmailOrUsername(trimmedEmail, trimmedUsername)
            ?.let { pendingRegistrationRepository.delete(it) }

        val passwordHashed = passwordEncoder.encode(password.trim())
            ?: throw PasswordHashFailedEx()

        val registration = pendingRegistrationRepository.saveAndFlush(
            PendingRegistrationEntity(
                email = trimmedEmail,
                username = trimmedUsername,
                hashedPassword = passwordHashed,
                expiresAt = Instant.now().plus(registrationExpiryDays, ChronoUnit.DAYS)
            )
        )

        val emailToken = emailVerificationService.createVerificationToken(trimmedEmail)

        eventPublisher.publish(
            event = UserEvent.RegistrationStarted(
                registrationId = registration.id!!,
                email = registration.email,
                username = registration.username,
                verificationToken = emailToken.token
            )
        )

        return registration.toPendingRegistration()
    }

    /**
     * Second step: the e-mail is already confirmed and the remaining details arrive, so
     * the user can be inserted with every column populated.
     */
    @Transactional
    fun completeRegistration(token: String, firstName: String, lastName: String): User {
        val verificationToken = emailVerificationService.requireUsableToken(token)
        val registration = verificationToken.pendingRegistration

        if (!registration.isVerified) {
            throw EmailNotVerifiedEx()
        }

        val savedUser = userRepository.saveAndFlush(
            UserEntity(
                email = registration.email,
                username = registration.username,
                hashedPassword = registration.hashedPassword,
                hasVerifiedEmail = true,
                firstName = firstName.trim(),
                lastName = lastName.trim()
            )
        ).toModel()

        emailVerificationService.discardTokensFor(registration)
        pendingRegistrationRepository.delete(registration)

        eventPublisher.publish(
            event = UserEvent.Created(
                userId = savedUser.id,
                email = savedUser.email,
                username = savedUser.username
            )
        )

        return savedUser
    }

    private fun PendingRegistrationEntity.toPendingRegistration() = PendingRegistration(
        id = id!!,
        email = email,
        username = username,
        isVerified = isVerified,
    )

    fun login(email: String, password: String): AuthenticatedUser {
        val user = userRepository.findByEmail(email) ?: throw InvalidCredentialsEx()

        if (!passwordEncoder.matches(password, user.hashedPassword)) {
            throw InvalidCredentialsEx()
        }

        if(!user.hasVerifiedEmail){
            throw EmailNotVerifiedEx()
        }

        return user.id?.let { userId ->
            val accessToken = jwtService.generateAccessToken(userId)
            val refreshToken = jwtService.generateRefreshToken(userId)
            storeRefreshToken(userId, refreshToken)
            AuthenticatedUser(
                user = user.toModel(),
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        } ?: throw UserNotFoundEx()
    }

    @Transactional
     fun refresh(refreshToken: String): AuthenticatedUser {
        if(!jwtService.validateRefreshToken(refreshToken)){
            throw InvalidTokenEx("Invalid refresh token")
        }

        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepository.findById(userId).orElseThrow { UserNotFoundEx() }
        val hashed = hashToken(refreshToken)

        return user.id?.let { userId ->
            refreshTokenRepository.findByUserIdAndHashedToken(
                userId = userId,
                hashedToken = hashed
            ) ?: throw InvalidTokenEx("Invalid refreshToken")

            refreshTokenRepository.deleteByUserIdAndHashedToken(
                userId = userId,
                hashedToken = hashed
            )

            val newAccessToken = jwtService.generateAccessToken(userId)
            val newRefreshToken = jwtService.generateRefreshToken(userId)

            storeRefreshToken(userId, newRefreshToken)

            AuthenticatedUser(
                user = user.toModel(),
                accessToken = newAccessToken,
                refreshToken = newRefreshToken
            )
        } ?: throw UserNotFoundEx()
    }

    private fun storeRefreshToken(userId: UserId, token: String) {
        val hashed = hashToken(token)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)

        refreshTokenRepository.save(
            RefreshTokenEntity(
                userId = userId,
                hashedToken = hashed,
                createdAt = Instant.now(),
                updatedAt = expiresAt
            )
        )
    }

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}