package com.evandhardspace.chatapp.infra.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@[Configuration ConfigurationProperties(prefix = "nginx")]
class NginxConfig(
    var trustedIps: List<String> = listOf(),
    var requireProxy: Boolean = true,
)
