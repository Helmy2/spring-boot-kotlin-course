package com.example.shopcraft.auth.service

import com.example.shopcraft.auth.entity.RefreshToken
import com.example.shopcraft.auth.entity.User

interface RefreshTokenService {
    fun createRefreshToken(user: User): RefreshToken
    fun rotateRefreshToken(rawToken: String): Pair<RefreshToken, User>
    fun revokeRefreshToken(rawToken: String)
    fun revokeAllUserTokens(user: User)
}
