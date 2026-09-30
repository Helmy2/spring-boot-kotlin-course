package com.example.shopcraft.auth.dto

import com.example.shopcraft.auth.entity.Role
import java.time.Instant

data class UserResponse(
    val id: Long,
    val email: String,
    val fullName: String,
    val role: Role,
    val createdAt: Instant
)
