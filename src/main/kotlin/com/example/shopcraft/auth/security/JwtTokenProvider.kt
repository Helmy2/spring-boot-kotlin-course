package com.example.shopcraft.auth.security

import com.example.shopcraft.auth.entity.Role
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties
) {

    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(StandardCharsets.UTF_8))
    }

    fun generateToken(email: String, role: Role): String {
        TODO("Step 2 - Mint a signed JSON Web Token containing the email subject, role list claim, issue timestamp, and expiration timestamp using the configured signing key.")
    }

    fun validateToken(token: String): Boolean {
        TODO("Step 3a - Parse and verify the signed JWT token with the signing key, returning true if valid or false if expired, malformed, or invalid.")
    }

    fun extractUsername(token: String): String {
        TODO("Step 3b - Parse the signed claims from the token and return the subject username.")
    }

    fun extractRoles(token: String): List<String> {
        TODO("Step 3c - Parse the signed claims from the token and return the list of assigned role authorities.")
    }
}
