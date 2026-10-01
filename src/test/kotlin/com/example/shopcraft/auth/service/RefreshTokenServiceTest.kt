package com.example.shopcraft.auth.service

import com.example.shopcraft.auth.entity.RefreshToken
import com.example.shopcraft.auth.entity.Role
import com.example.shopcraft.auth.entity.User
import com.example.shopcraft.auth.repository.RefreshTokenRepository
import com.example.shopcraft.auth.security.JwtProperties
import com.example.shopcraft.common.exception.TokenRefreshException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Instant

class RefreshTokenServiceTest {

    private val refreshTokenRepository: RefreshTokenRepository = mock()
    private val jwtProperties = JwtProperties(refreshTokenExpirationMs = 604800000)
    private val refreshTokenService: RefreshTokenService = RefreshTokenServiceImpl(refreshTokenRepository, jwtProperties)

    private val sampleUser = User(
        id = 1L,
        email = "test@example.com",
        passwordHash = "hash",
        fullName = "Test User",
        role = Role.ROLE_USER
    )

    @Test
    fun `given valid user, when createRefreshToken, then saves and returns active refresh token`() {
        whenever(refreshTokenRepository.save(any<RefreshToken>())).thenAnswer { invocation ->
            invocation.arguments[0] as RefreshToken
        }

        val created = refreshTokenService.createRefreshToken(sampleUser)

        assertThat(created.token).isNotBlank
        assertThat(created.user).isEqualTo(sampleUser)
        assertThat(created.revoked).isFalse
        assertThat(created.expiryDate).isAfter(Instant.now())
        verify(refreshTokenRepository).save(any<RefreshToken>())
    }

    @Test
    fun `given valid non-expired refresh token, when rotateRefreshToken, then revokes old token and issues new token`() {
        val oldToken = RefreshToken(
            id = 10L,
            token = "old-valid-token",
            user = sampleUser,
            expiryDate = Instant.now().plusSeconds(3600),
            revoked = false
        )
        whenever(refreshTokenRepository.findByToken("old-valid-token")).thenReturn(oldToken)
        whenever(refreshTokenRepository.save(any<RefreshToken>())).thenAnswer { invocation ->
            invocation.arguments[0] as RefreshToken
        }

        val (newToken, user) = refreshTokenService.rotateRefreshToken("old-valid-token")

        assertThat(oldToken.revoked).isTrue
        assertThat(newToken.token).isNotEqualTo("old-valid-token")
        assertThat(newToken.revoked).isFalse
        assertThat(user.email).isEqualTo(sampleUser.email)
    }

    @Test
    fun `given non-existent refresh token, when rotateRefreshToken, then throws TokenRefreshException`() {
        whenever(refreshTokenRepository.findByToken("missing-token")).thenReturn(null)

        assertThatThrownBy {
            refreshTokenService.rotateRefreshToken("missing-token")
        }.isInstanceOf(TokenRefreshException::class.java)
            .hasMessageContaining("Refresh token was not found")
    }

    @Test
    fun `given expired refresh token, when rotateRefreshToken, then deletes token and throws TokenRefreshException`() {
        val expiredToken = RefreshToken(
            id = 11L,
            token = "expired-token",
            user = sampleUser,
            expiryDate = Instant.now().minusSeconds(3600),
            revoked = false
        )
        whenever(refreshTokenRepository.findByToken("expired-token")).thenReturn(expiredToken)

        assertThatThrownBy {
            refreshTokenService.rotateRefreshToken("expired-token")
        }.isInstanceOf(TokenRefreshException::class.java)
            .hasMessageContaining("Refresh token has expired")

        verify(refreshTokenRepository).delete(expiredToken)
    }

    @Test
    fun `given revoked refresh token, when rotateRefreshToken, then detects token reuse revokes all user tokens and throws TokenRefreshException`() {
        val revokedToken = RefreshToken(
            id = 12L,
            token = "revoked-token",
            user = sampleUser,
            expiryDate = Instant.now().plusSeconds(3600),
            revoked = true
        )
        val otherToken = RefreshToken(
            id = 13L,
            token = "another-token",
            user = sampleUser,
            expiryDate = Instant.now().plusSeconds(3600),
            revoked = false
        )
        whenever(refreshTokenRepository.findByToken("revoked-token")).thenReturn(revokedToken)
        whenever(refreshTokenRepository.findAllByUser(sampleUser)).thenReturn(listOf(revokedToken, otherToken))

        assertThatThrownBy {
            refreshTokenService.rotateRefreshToken("revoked-token")
        }.isInstanceOf(TokenRefreshException::class.java)
            .hasMessageContaining("Potential token reuse detected")

        assertThat(otherToken.revoked).isTrue
        verify(refreshTokenRepository).saveAll(listOf(revokedToken, otherToken))
    }

    @Test
    fun `given existing refresh token, when revokeRefreshToken, then marks token as revoked`() {
        val token = RefreshToken(
            id = 14L,
            token = "token-to-revoke",
            user = sampleUser,
            expiryDate = Instant.now().plusSeconds(3600),
            revoked = false
        )
        whenever(refreshTokenRepository.findByToken("token-to-revoke")).thenReturn(token)

        refreshTokenService.revokeRefreshToken("token-to-revoke")

        assertThat(token.revoked).isTrue
        verify(refreshTokenRepository).save(token)
    }

    @Test
    fun `given active tokens for user, when revokeAllUserTokens, then revokes all tokens`() {
        val token1 = RefreshToken(id = 15L, token = "t1", user = sampleUser, expiryDate = Instant.now().plusSeconds(3600), revoked = false)
        val token2 = RefreshToken(id = 16L, token = "t2", user = sampleUser, expiryDate = Instant.now().plusSeconds(3600), revoked = false)
        whenever(refreshTokenRepository.findAllByUser(sampleUser)).thenReturn(listOf(token1, token2))

        refreshTokenService.revokeAllUserTokens(sampleUser)

        assertThat(token1.revoked).isTrue
        assertThat(token2.revoked).isTrue
        verify(refreshTokenRepository).saveAll(listOf(token1, token2))
    }
}
