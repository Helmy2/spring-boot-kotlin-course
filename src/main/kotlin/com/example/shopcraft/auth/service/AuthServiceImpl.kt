package com.example.shopcraft.auth.service

import com.example.shopcraft.auth.dto.AuthResponse
import com.example.shopcraft.auth.dto.LoginRequest
import com.example.shopcraft.auth.dto.RegisterRequest
import com.example.shopcraft.auth.dto.UserResponse
import com.example.shopcraft.auth.repository.UserRepository
import com.example.shopcraft.auth.security.JwtTokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) : AuthService {

    @Transactional
    override fun register(request: RegisterRequest): AuthResponse {
        TODO("Step 5 - Verify that the email is not already registered. Hash the plain-text password using the configured password encoder, persist the new User entity with default customer role, and return an AuthResponse containing a newly minted JWT token and user profile.")
    }

    override fun login(request: LoginRequest): AuthResponse {
        TODO("Step 6a - Find the user by email, verify the supplied password against the stored BCrypt hash, and return an AuthResponse containing a newly minted JWT token and user profile.")
    }

    override fun getCurrentUser(): UserResponse {
        TODO("Step 6b - Retrieve the authenticated user principal from the SecurityContextHolder, look up their profile in the repository, and return a UserResponse.")
    }
}
