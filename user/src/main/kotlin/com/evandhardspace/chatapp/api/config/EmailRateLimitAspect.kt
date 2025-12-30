package com.evandhardspace.chatapp.api.config

import com.evandhardspace.chatapp.api.dto.EmailRequest
import com.evandhardspace.chatapp.infra.ratelimiting.Backoff
import com.evandhardspace.chatapp.infra.ratelimiting.EmailRateLimiter
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import util.findIsInstance

@Aspect
@Component
class EmailRateLimitAspect(
    private val emailRateLimiter: EmailRateLimiter
) {

    @Around("@annotation(emailRateLimit)")
    fun around(joinPoint: ProceedingJoinPoint, emailRateLimit: EmailRateLimit) {
        val args = joinPoint.args
        val email = args.findIsInstance<EmailRequest>()?.email
            ?: throw IllegalArgumentException("EmailRequest not found in method arguments.")

        emailRateLimiter.withRateLimit(
            email = email,
            backoff = Backoff(emailRateLimit.backoffSeconds),
            action = joinPoint::proceed,
        )
    }
}
