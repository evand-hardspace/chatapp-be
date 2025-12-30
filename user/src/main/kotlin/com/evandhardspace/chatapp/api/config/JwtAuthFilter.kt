package com.evandhardspace.chatapp.api.config

import com.evandhardspace.chatapp.domain.model.Token
import com.evandhardspace.chatapp.service.auth.AuthTokenService
import com.evandhardspace.chatapp.service.auth.isBearer
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val authTokenService: AuthTokenService,
): OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authHeader: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        if(authHeader.isBearer()) {
            val accessToken = Token.AccessToken(authHeader)
            if(authTokenService.isValidToken(accessToken)) {
                val userId = authTokenService.getUserId(accessToken)
                val auth = UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    emptyList()
                )
                SecurityContextHolder.getContext().authentication = auth
            }
        }
        filterChain.doFilter(request, response)
    }
}