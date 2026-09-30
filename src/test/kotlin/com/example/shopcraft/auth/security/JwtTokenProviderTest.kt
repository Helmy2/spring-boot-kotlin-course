package com.example.shopcraft.auth.security

import com.example.shopcraft.auth.entity.Role
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class JwtTokenProviderTest {

    private val jwtProperties = JwtProperties(
        secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
        expirationMs = 3600000,
        tokenPrefix = "Bearer ",
        headerString = "Authorization"
    )
    private val tokenProvider = JwtTokenProvider(jwtProperties)

    @Test
    fun `given user email and role, when generateToken, then creates valid signed jjwt token with subject and role claims`() {
        val email = "john.doe@example.com"
        val role = Role.ROLE_ADMIN

        val token = tokenProvider.generateToken(email, role)

        assertThat(token).isNotBlank
        assertThat(token.split(".")).hasSize(3)
        assertThat(tokenProvider.validateToken(token)).isTrue
        assertThat(tokenProvider.extractUsername(token)).isEqualTo(email)
        assertThat(tokenProvider.extractRoles(token)).containsExactly("ROLE_ADMIN")
    }

    @Test
    fun `given valid token, when validateToken, then returns true`() {
        val token = tokenProvider.generateToken("jane.doe@example.com", Role.ROLE_USER)

        val isValid = tokenProvider.validateToken(token)

        assertThat(isValid).isTrue
    }

    @Test
    fun `given malformed token, when validateToken, then returns false`() {
        val malformedToken = "not.a.valid.jwt.token"

        val isValid = tokenProvider.validateToken(malformedToken)

        assertThat(isValid).isFalse
    }

    @Test
    fun `given expired token, when validateToken, then returns false`() {
        val expiredProperties = JwtProperties(
            secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
            expirationMs = -5000
        )
        val expiredProvider = JwtTokenProvider(expiredProperties)
        val expiredToken = expiredProvider.generateToken("expired@example.com", Role.ROLE_USER)

        val isValid = tokenProvider.validateToken(expiredToken)

        assertThat(isValid).isFalse
    }

    @Test
    fun `given valid token, when extractUsername and extractRoles, then returns expected claims`() {
        val token = tokenProvider.generateToken("admin@example.com", Role.ROLE_ADMIN)

        val username = tokenProvider.extractUsername(token)
        val roles = tokenProvider.extractRoles(token)

        assertThat(username).isEqualTo("admin@example.com")
        assertThat(roles).containsExactly("ROLE_ADMIN")
    }
}
