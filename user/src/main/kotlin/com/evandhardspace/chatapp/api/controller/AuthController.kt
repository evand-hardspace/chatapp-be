package com.evandhardspace.chatapp.api.controller

import com.evandhardspace.chatapp.api.config.IpRateLimit
import com.evandhardspace.chatapp.api.dto.AuthenticatedUserDto
import com.evandhardspace.chatapp.api.dto.ChangePasswordRequest
import com.evandhardspace.chatapp.api.dto.EmailRequest
import com.evandhardspace.chatapp.api.dto.LoginRequest
import com.evandhardspace.chatapp.api.dto.RefreshRequest
import com.evandhardspace.chatapp.api.dto.RegisterRequest
import com.evandhardspace.chatapp.api.dto.ResetPasswordRequest
import com.evandhardspace.chatapp.api.dto.UserDto
import com.evandhardspace.chatapp.api.mapper.toAuthenticatedUserDto
import com.evandhardspace.chatapp.api.mapper.toUserDto
import com.evandhardspace.chatapp.api.util.requestUserId
import com.evandhardspace.chatapp.infra.ratelimiting.EmailRateLimiter
import com.evandhardspace.chatapp.service.auth.AuthService
import com.evandhardspace.chatapp.service.auth.EmailVerificationService
import com.evandhardspace.chatapp.service.auth.PasswordResetService
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
    private val emailRateLimiter: EmailRateLimiter,
) {

    @PostMapping("/register")
    @IpRateLimit(
        requests = 10,
        duration = 1,
        timeUnit = TimeUnit.HOURS,
    )
    fun register(
        @[Valid RequestBody] body: RegisterRequest,
    ): UserDto {
        return authService.register(
            email = body.email,
            username = body.username,
            password = body.password,
        ).toUserDto()
    }

    @PostMapping("/login")
    @IpRateLimit(
        requests = 10,
        duration = 1,
        timeUnit = TimeUnit.HOURS,
    )
    fun login(
        @RequestBody body: LoginRequest,
    ): AuthenticatedUserDto {
        return authService.login(
            email = body.email,
            password = body.password,
        ).toAuthenticatedUserDto()
    }

    @PostMapping("/logout")
    fun logout(
        @RequestBody body: RefreshRequest,
    ) {
        return authService.logout(
            refreshToken = body.refreshToken,
        )
    }

    @PostMapping("/resend-verification")
    @IpRateLimit(
        requests = 10,
        duration = 1,
        timeUnit = TimeUnit.HOURS,
    )
    fun resentVerification(
        @[Valid RequestBody] body: EmailRequest,
    ) {
        emailRateLimiter.withRateLimit(email = body.email) {
            emailVerificationService.resendVerificationEmail(email = body.email)
        }
    }

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody body: RefreshRequest,
    ): AuthenticatedUserDto {
        return authService.refresh(
            refreshToken = body.refreshToken,
        ).toAuthenticatedUserDto()
    }

    @GetMapping("/verify")
    fun verifyEmail(
        @RequestParam token: String,
    ) {
        emailVerificationService.verifyEmail(
            token = token,
        )
    }

    @PostMapping("/reset-password")
    fun resetPassword(
        @[Valid RequestBody] body: ResetPasswordRequest,
    ) {
        passwordResetService.resetPassword(
            token = body.token,
            newPassword = body.newPassword,
        )
    }

    @PostMapping("/change-password")
    fun changePassword(
        @[Valid RequestBody] body: ChangePasswordRequest,
    ) {
        passwordResetService.changePassword(
            userId = requestUserId,
            oldPassword = body.oldPassword,
            newPassword = body.newPassword,
        )
    }

    @PostMapping("/forgot-password")
    @IpRateLimit(
        requests = 10,
        duration = 1,
        timeUnit = TimeUnit.HOURS,
    )
    fun forgotPassword(
        @[Valid RequestBody] body: EmailRequest,
    ) {
        passwordResetService.requestPasswordReset(body.email)
    }
}