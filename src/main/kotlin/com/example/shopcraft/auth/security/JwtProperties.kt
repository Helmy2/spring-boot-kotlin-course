package com.example.shopcraft.auth.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.jwt")
data class JwtProperties(
    val secret: String = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    val expirationMs: Long = 86400000,
    val tokenPrefix: String = "Bearer ",
    val headerString: String = "Authorization"
)
