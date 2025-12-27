package com.evandhardspace.chatapp.infra.ratelimiting

import com.evandhardspace.chatapp.infra.config.NginxConfig
import com.sun.org.slf4j.internal.LoggerFactory
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.web.util.matcher.IpAddressMatcher
import org.springframework.stereotype.Component
import java.net.Inet4Address
import java.net.Inet6Address

@Component
class IpResolver(
    private val nginxConfig: NginxConfig
) {

    private val logger = LoggerFactory.getLogger(IpResolver::class.java)

    private val trustedMatchers: List<IpAddressMatcher> = nginxConfig
        .trustedIps
        .filter { it.isNotBlank() }
        .map { proxy ->
            val cidr = when {
                proxy.contains("/") -> proxy
                proxy.contains(":") -> "$proxy/128"
                else -> "$proxy/32"
            }
            IpAddressMatcher(cidr)
        }

    private val String.isPrivateIp: Boolean
        get() = PRIVATE_IP_RANGES.any { it.matches(this) }

    private val String.isFromTrustedProxy: Boolean
        get() = trustedMatchers.any { it.matches(this) }

    private fun String.validatedAndNormalizedIp(
        headerName: String,
        proxyIp: String,
    ): String? {
        val trimmedIp = this.trim()

        if (trimmedIp.isBlank() || trimmedIp in INVALID_IPS) {
            logger.debug("Invalid IP in $headerName: $this from proxy $proxyIp")
            return null
        }

        return try {
            val inetAddr = when {
                ":" in trimmedIp -> Inet6Address.getByName(trimmedIp)
                trimmedIp.matches("\\d+\\.\\d+\\.\\d+\\.\\d+".toRegex()) -> Inet4Address.getByName(trimmedIp)
                else -> {
                    logger.warn("Invalid IP format in $headerName: $trimmedIp from proxy $proxyIp")
                    return null
                }
            }

            if (inetAddr.hostAddress.isPrivateIp) logger.debug("Private IP in $headerName: $trimmedIp from proxy $proxyIp")
            inetAddr.hostAddress
        } catch (e: Exception) {
            logger.warn("Invalid IP format in $headerName: $trimmedIp from proxy $proxyIp", e)
            null
        }
    }

    fun getClientIp(request: HttpServletRequest): String {
        val remoteAddr = request.remoteAddr
        if (remoteAddr.isFromTrustedProxy.not()) {
            if (nginxConfig.requireProxy) {
                logger.warn("Direct connection attempt from $remoteAddr")
                throw SecurityException("No valid client IP in proxy headers.")
            }

            return remoteAddr
        }

        val clientIp = extractFromXRealIp(request, remoteAddr)

        if(clientIp == null) {
            logger.warn("No valid client IP in proxy headers")
            if(nginxConfig.requireProxy) {
                throw SecurityException("No valid client IP in proxy headers.")
            }
        }

        return clientIp ?: remoteAddr
    }

    private fun extractFromXRealIp(
        request: HttpServletRequest,
        proxyIp: String,
    ): String? = request.getHeader("X-Real-IP")
        ?.validatedAndNormalizedIp(
            headerName = "X-Real-IP",
            proxyIp = proxyIp,
        )


    private companion object {
        val PRIVATE_IP_RANGES = listOf(
            "10.0.0.0/8",
            "172.16.0.0/12",
            "192.168.0.0/16",
            "127.0.0.0/8",
            "::1/128",
            "fc00::/7",
            "fe80::/10"
        ).map(::IpAddressMatcher)

        val INVALID_IPS = listOf(
            "unknown",
            "unavailable",
            "0.0.0.0",
            "::"
        )
    }
}