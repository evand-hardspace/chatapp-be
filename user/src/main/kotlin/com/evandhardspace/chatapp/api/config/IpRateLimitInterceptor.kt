package com.evandhardspace.chatapp.api.config

import com.evandhardspace.chatapp.domain.exception.RateLimitException
import com.evandhardspace.chatapp.infra.ratelimiting.IpRateLimiter
import com.evandhardspace.chatapp.infra.ratelimiting.IpResolver
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import java.time.Duration

@Component
class IpRateLimitInterceptor(
    private val ipRateLimiter: IpRateLimiter,
    private val ipResolver: IpResolver,
    @param:Value($$"${chatapp.rate-limit.ip.apply-limit}")
    private val applyLimit: Boolean,
) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (applyLimit && handler is HandlerMethod) {
            val annotation = handler.getMethodAnnotation(IpRateLimit::class.java)
            if (annotation != null) {
                val clientIp = ipResolver.getClientIp(request)
                return try {
                    ipRateLimiter.withIpRateLimit(
                        ipAddress = clientIp,
                        resetsIn = Duration.of(annotation.duration, annotation.timeUnit.toChronoUnit()),
                        maxRequestsPerIp = annotation.requests
                    ) { true }
                } catch (_: RateLimitException) {
                    response.sendError(429)
                    false
                }
            }
        }
        return super.preHandle(request, response, handler)
    }
}