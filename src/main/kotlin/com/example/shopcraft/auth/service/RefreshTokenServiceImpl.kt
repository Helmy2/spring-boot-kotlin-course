package com.example.shopcraft.auth.service

import com.example.shopcraft.auth.entity.RefreshToken
import com.example.shopcraft.auth.entity.User
import com.example.shopcraft.auth.repository.RefreshTokenRepository
import com.example.shopcraft.auth.security.JwtProperties
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class RefreshTokenServiceImpl(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtProperties: JwtProperties
) : RefreshTokenService {

    @Transactional
    override fun createRefreshToken(user: User): RefreshToken {
        TODO("Step 1 - Mint a new random refresh token for the user with expiration configured from JwtProperties and persist it")
    }

    @Transactional
    override fun rotateRefreshToken(rawToken: String): Pair<RefreshToken, User> {
        TODO("Step 2 - Retrieve token from repository, check if revoked to trigger reuse detection across all user tokens, verify token is not expired, revoke old token, and issue a fresh refresh token")
    }

    @Transactional
    override fun revokeRefreshToken(rawToken: String) {
        TODO("Step 3 - Retrieve the refresh token and mark it as revoked")
    }

    @Transactional
    override fun revokeAllUserTokens(user: User) {
        TODO("Step 4 - Revoke all active refresh tokens associated with the user to terminate all sessions")
    }
}
