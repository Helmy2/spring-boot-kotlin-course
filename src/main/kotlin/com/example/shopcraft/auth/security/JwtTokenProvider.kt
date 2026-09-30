package com.example.shopcraft.auth.security

import com.example.shopcraft.auth.entity.Role
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties
) {

    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(StandardCharsets.UTF_8))
    }

    fun generateToken(email: String, role: Role): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtProperties.expirationMs)

        return Jwts.builder()
            .subject(email)
            .claim("roles", listOf(role.name))
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun extractUsername(token: String): String {
        val claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
        return claims.subject
    }

    fun extractRoles(token: String): List<String> {
        val claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
        val rolesObj = claims["roles"]
        return when (rolesObj) {
            is List<*> -> rolesObj.filterIsInstance<String>()
            is String -> listOf(rolesObj)
            else -> emptyList()
        }
    }
}
