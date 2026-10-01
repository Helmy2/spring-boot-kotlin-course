package com.example.shopcraft.auth.dto

data class AuthResponse(
    val token: String,
    val refreshToken: String? = null,
    val tokenType: String = "Bearer",
    val user: UserResponse
)
