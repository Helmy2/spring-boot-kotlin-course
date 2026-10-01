package com.example.shopcraft.auth.service

import com.example.shopcraft.auth.entity.RefreshToken
import com.example.shopcraft.auth.entity.User
import com.example.shopcraft.auth.repository.RefreshTokenRepository
import com.example.shopcraft.auth.security.JwtProperties
import com.example.shopcraft.common.exception.TokenRefreshException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
@Transactional(readOnly = true)
class RefreshTokenServiceImpl(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtProperties: JwtProperties
) : RefreshTokenService {

    @Transactional
    override fun createRefreshToken(user: User): RefreshToken {
        val refreshToken = RefreshToken(
            token = UUID.randomUUID().toString(),
            user = user,
            expiryDate = Instant.now().plusMillis(jwtProperties.refreshTokenExpirationMs)
        )
        return refreshTokenRepository.save(refreshToken)
    }

    @Transactional
    override fun rotateRefreshToken(rawToken: String): Pair<RefreshToken, User> {
        val token = refreshTokenRepository.findByToken(rawToken)
            ?: throw TokenRefreshException(rawToken, "Refresh token was not found")

        if (token.revoked) {
            revokeAllUserTokens(token.user)
            throw TokenRefreshException(
                rawToken,
                "Refresh token has been revoked. Potential token reuse detected. All user sessions have been terminated."
            )
        }

        if (token.isExpired()) {
            refreshTokenRepository.delete(token)
            throw TokenRefreshException(rawToken, "Refresh token has expired. Please sign in again.")
        }

        token.revoked = true
        refreshTokenRepository.save(token)

        val newRefreshToken = createRefreshToken(token.user)
        return Pair(newRefreshToken, token.user)
    }

    @Transactional
    override fun revokeRefreshToken(rawToken: String) {
        val token = refreshTokenRepository.findByToken(rawToken) ?: return
        token.revoked = true
        refreshTokenRepository.save(token)
    }

    @Transactional
    override fun revokeAllUserTokens(user: User) {
        val userTokens = refreshTokenRepository.findAllByUser(user)
        userTokens.forEach { it.revoked = true }
        refreshTokenRepository.saveAll(userTokens)
    }
}
