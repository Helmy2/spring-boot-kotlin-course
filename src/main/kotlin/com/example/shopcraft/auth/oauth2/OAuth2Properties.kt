package com.example.shopcraft.auth.oauth2

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.oauth2")
data class OAuth2Properties(
    val authorizedRedirectUri: String = "http://localhost:8080/api/v1/auth/oauth2/callback"
)
