package com.example.shopcraft.auth.repository

import com.example.shopcraft.auth.entity.RefreshToken
import com.example.shopcraft.auth.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByToken(token: String): RefreshToken?
    fun findAllByUser(user: User): List<RefreshToken>

    @Modifying
    @Transactional
    fun deleteByUser(user: User): Int

    @Modifying
    @Transactional
    fun deleteByExpiryDateBefore(instant: Instant): Int
}
