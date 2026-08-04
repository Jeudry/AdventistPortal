package com.adventistportal.core.domain.events.user

import com.adventistportal.core.domain.events.AdventistPortalEvent
import com.adventistportal.core.domain.types.UserId
import java.time.Instant
import java.util.UUID

sealed class UserEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val exchange: String = UserEventConstants.USER_EXCHANGE,
    override val occurredAt: Instant = Instant.now()
): AdventistPortalEvent {

    /** A registration was started. No user exists yet — only a pending registration. */
    data class RegistrationStarted (
        val registrationId: UUID,
        val email: String,
        val username: String,
        val verificationToken: String,
        override val eventKey: String = UserEventConstants.USER_REGISTRATION_STARTED
    ): UserEvent()

    /** The registration completed and the user now exists. */
    data class Created (
        val userId: UserId,
        val email: String,
        val username: String,
        override val eventKey: String = UserEventConstants.USER_CREATED_KEY
    ): UserEvent(), AdventistPortalEvent

    /** The e-mail was confirmed. The user still does not exist — details are pending. */
    data class Verified (
        val registrationId: UUID,
        val email: String,
        val username: String,
        override val eventKey: String = UserEventConstants.USER_VERIFIED
    ): UserEvent(), AdventistPortalEvent

    data class RequestResendVerification (
        val registrationId: UUID,
        val email: String,
        val username: String,
        val verificationToken: String,
        override val eventKey: String = UserEventConstants.USER_REQUEST_RESEND_VERIFICATION
    ): UserEvent(), AdventistPortalEvent

    data class RequestResetPassword (
        val userId: UserId,
        val email: String,
        val username: String,
        val verificationToken: String,
        val expiresInMinutes: Long,
        override val eventKey: String = UserEventConstants.USER_REQUEST_RESET_PASSWORD
    ): UserEvent(), AdventistPortalEvent
}