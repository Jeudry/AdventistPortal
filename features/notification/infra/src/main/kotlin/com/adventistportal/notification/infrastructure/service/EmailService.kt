package com.adventistportal.notification.infrastructure.service

import com.adventistportal.core.domain.types.UserId
import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.time.Duration

@Service
class EmailService(
    private val javaMailSender: JavaMailSender,
    private val templateService: EmailTemplateService,
    @param:Value("\${adventistportal.email.from}")
    private val emailFrom: String,
    @param:Value("\${adventistportal.email.url}")
    private val baseUrl: String,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun sendVerificationEmail(
        email: String,
        username: String,
        registrationId: UUID,
        token: String
    ){
        logger.info("Sending verification email for registration: $registrationId")

        val verificationUrl = UriComponentsBuilder
            .fromUriString("$baseUrl/api/auth/verify")
            .queryParam("token", token)
            .build()
            .toUriString()

        val htmlContent = templateService.processTemplate(
            "emails/account-verification",
            mapOf(
                "username" to username,
                "verificationUrl" to verificationUrl
            )
        )

        sendHtmlEmail(email, "Verify your AdventistPortal account", htmlContent)
    }

    fun sendPasswordResetEmail(
        email: String,
        username: String,
        registrationId: UUID,
        token: String,
        expiresInMinutes: Duration
    ){
        logger.info("Sending password reset email for user: $registrationId")

        val resetPasswordUrl = UriComponentsBuilder
            .fromUriString("$baseUrl/api/auth/reset-password")
            .queryParam("token", token)
            .build()
            .toUriString()

        val htmlContent = templateService.processTemplate(
            "emails/account-verification",
            mapOf(
                "username" to username,
                "resetPasswordUrl" to resetPasswordUrl,
                "expiresInMinutes" to expiresInMinutes.toMinutes()
            )
        )

        sendHtmlEmail(email, "Reset your Adventist password", htmlContent)
    }


    private fun sendHtmlEmail(
        to: String,
        subject: String,
        htmlContent: String
    ){
        val message = javaMailSender.createMimeMessage()
        MimeMessageHelper(message, true, "UTF-8").apply {
            setTo(to)
            setSubject(subject)
            setText(htmlContent, true)
            setFrom(emailFrom)
        }

        // Deliberately not caught. Catching it acknowledged the message, so a failed send
        // meant an e-mail nobody ever received and nothing anywhere saying so. Letting it
        // out gives the listener its retries, and then the dead-letter queue.
        javaMailSender.send(message)
    }
}