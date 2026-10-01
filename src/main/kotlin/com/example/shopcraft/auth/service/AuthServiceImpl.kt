package com.example.shopcraft.auth.service

import com.example.shopcraft.auth.dto.AuthResponse
import com.example.shopcraft.auth.dto.LoginRequest
import com.example.shopcraft.auth.dto.RegisterRequest
import com.example.shopcraft.auth.dto.UserResponse
import com.example.shopcraft.auth.dto.RefreshTokenRequest
import com.example.shopcraft.auth.entity.Role
import com.example.shopcraft.auth.entity.User
import com.example.shopcraft.auth.repository.UserRepository
import com.example.shopcraft.auth.security.JwtTokenProvider
import com.example.shopcraft.common.exception.DuplicateResourceException
import com.example.shopcraft.common.exception.ResourceNotFoundException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshTokenService: RefreshTokenService
) : AuthService {

    @Transactional
    override fun register(request: RegisterRequest): AuthResponse {
        val normalizedEmail = request.email.trim().lowercase()
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw DuplicateResourceException("User with email '$normalizedEmail' already exists")
        }

        val user = User(
            email = normalizedEmail,
            passwordHash = passwordEncoder.encode(request.password) ?: "",
            fullName = request.fullName.trim(),
            role = request.role ?: Role.ROLE_USER
        )
        val savedUser = userRepository.save(user)
        val token = jwtTokenProvider.generateToken(savedUser.email, savedUser.role)
        val refreshToken = refreshTokenService.createRefreshToken(savedUser)

        return AuthResponse(
            token = token,
            refreshToken = refreshToken.token,
            tokenType = "Bearer",
            user = savedUser.toResponse()
        )
    }

    @Transactional
    override fun login(request: LoginRequest): AuthResponse {
        val normalizedEmail = request.email.trim().lowercase()
        val user = userRepository.findByEmail(normalizedEmail)
            ?: throw BadCredentialsException("Invalid email or password")

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw BadCredentialsException("Invalid email or password")
        }

        val token = jwtTokenProvider.generateToken(user.email, user.role)
        val refreshToken = refreshTokenService.createRefreshToken(user)
        return AuthResponse(
            token = token,
            refreshToken = refreshToken.token,
            tokenType = "Bearer",
            user = user.toResponse()
        )
    }

    @Transactional
    override fun refreshToken(request: RefreshTokenRequest): AuthResponse {
        val (newRefreshToken, user) = refreshTokenService.rotateRefreshToken(request.refreshToken)
        val token = jwtTokenProvider.generateToken(user.email, user.role)

        return AuthResponse(
            token = token,
            refreshToken = newRefreshToken.token,
            tokenType = "Bearer",
            user = user.toResponse()
        )
    }

    @Transactional
    override fun logout(request: RefreshTokenRequest) {
        refreshTokenService.revokeRefreshToken(request.refreshToken)
    }

    override fun getCurrentUser(): UserResponse {
        val authentication = SecurityContextHolder.getContext().authentication
        val email = authentication?.name
            ?: throw BadCredentialsException("No authenticated user found in security context")

        val user = userRepository.findByEmail(email)
            ?: throw ResourceNotFoundException("User not found with email: $email")

        return user.toResponse()
    }
}
