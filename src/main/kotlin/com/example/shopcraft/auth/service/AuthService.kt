package com.example.shopcraft.auth.service

import com.example.shopcraft.auth.dto.AuthResponse
import com.example.shopcraft.auth.dto.LoginRequest
import com.example.shopcraft.auth.dto.RefreshTokenRequest
import com.example.shopcraft.auth.dto.RegisterRequest
import com.example.shopcraft.auth.dto.UserResponse

interface AuthService {
    fun register(request: RegisterRequest): AuthResponse
    fun login(request: LoginRequest): AuthResponse
    fun refreshToken(request: RefreshTokenRequest): AuthResponse
    fun logout(request: RefreshTokenRequest)
    fun getCurrentUser(): UserResponse
}
