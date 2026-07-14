package com.adventistportal.user.api.controller

import com.adventistportal.core.api.utils.requestUserId
import com.adventistportal.user.api.config.IpRateLimit
import com.adventistportal.user.api.dtos.AuthenticatedUserDto
import com.adventistportal.user.api.dtos.ChangePasswordRequest
import com.adventistportal.user.api.dtos.EmailRequest
import com.adventistportal.user.api.dtos.LoginRequest
import com.adventistportal.user.api.dtos.RefreshTokenRequest
import com.adventistportal.user.api.dtos.ResetPasswordRequest
import com.adventistportal.user.api.dtos.UserDto
import com.adventistportal.user.api.mappers.toDto
import com.adventistportal.user.api.dtos.RegisterRequest
import com.adventistportal.user.infrastructure.rate_limiting.EmailRateLimiter
import com.adventistportal.user.service.AuthService
import com.adventistportal.user.service.EmailVerificationService
import com.adventistportal.user.service.PasswordResetService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val emailVerificationService: EmailVerificationService,
    private val passwordResetService: PasswordResetService,
    private val emailRateLimiter: EmailRateLimiter
) {
    @PostMapping("/register")
    @IpRateLimit(
        requests = 10,
        duration = 1L,
        unit = TimeUnit.HOURS
    )
    fun register(
      @RequestBody registerRequest: RegisterRequest
    ): UserDto {
        return authService.register(
            username = registerRequest.username,
            email = registerRequest.email,
            password = registerRequest.password
        ).toDto()
    }

    @PostMapping("/login")
    @IpRateLimit(
        requests = 25,
        duration = 1L,
        unit = TimeUnit.HOURS
    )
    fun login(
        @RequestBody loginRequest: LoginRequest
    ): AuthenticatedUserDto {
        return authService.login(
            email = loginRequest.email,
            password = loginRequest.password
        ).toDto()
    }

    @PostMapping("/refresh")
    @IpRateLimit(
        requests = 25,
        duration = 1L,
        unit = TimeUnit.HOURS
    )
    fun refresh(
        @RequestBody refreshTokenRequest: RefreshTokenRequest
    ): AuthenticatedUserDto {
        return authService.refresh(refreshTokenRequest.refreshToken).toDto()
    }

    @PostMapping("/resend-verification")
    @IpRateLimit(
        requests = 25,
        duration = 1L,
        unit = TimeUnit.HOURS
    )
    fun resendVerification(
        @Valid @RequestBody emailRequest: EmailRequest
    ) {
        emailRateLimiter.withRateLimit(emailRequest.email){
            emailVerificationService.resendVerificationEmail(emailRequest.email)
        }
    }

    @GetMapping("/verify")
    @IpRateLimit(
        requests = 25,
        duration = 1L,
        unit = TimeUnit.HOURS
    )
    fun verifyEmail(
        @RequestParam token: String
    ){
        emailVerificationService.verifyEmail(token)
    }

    @PostMapping("/forgot-password")
    @IpRateLimit(
        requests = 25,
        duration = 1L,
        unit = TimeUnit.HOURS
    )
    fun forgotPassword(
        @Valid @RequestBody body: EmailRequest
    ){
        passwordResetService.requestPasswordReset(body.email)
    }

    @PostMapping("/reset-password")
    @IpRateLimit(
        requests = 25,
        duration = 1L,
        unit = TimeUnit.HOURS
    )
    fun resetPassword(
        @Valid @RequestBody body: ResetPasswordRequest
    ){
        passwordResetService.resetPassword(
            token = body.token,
            password = body.newPassword
        )
    }

    @PostMapping("/change-password")
    fun changePassword(
        @Valid @RequestBody body: ChangePasswordRequest
    ){
         passwordResetService.changePassword(
            newPassword = body.newPassword,
            oldPassword = body.oldPassword,
            userId = requestUserId
        )
    }
}