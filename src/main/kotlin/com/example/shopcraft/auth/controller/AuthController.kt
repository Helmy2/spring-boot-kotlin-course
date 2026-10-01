package com.example.shopcraft.auth.controller

import com.example.shopcraft.auth.dto.AuthResponse
import com.example.shopcraft.auth.dto.LoginRequest
import com.example.shopcraft.auth.dto.RefreshTokenRequest
import com.example.shopcraft.auth.dto.RegisterRequest
import com.example.shopcraft.auth.dto.UserResponse
import com.example.shopcraft.auth.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        val response = authService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        val response = authService.login(request)
        return ResponseEntity.ok(response)
    }

    // TODO: Map to POST /refresh, accept validated RefreshTokenRequest in the request body, and return 200 OK with AuthResponse
    fun refreshToken(request: RefreshTokenRequest): ResponseEntity<AuthResponse> {
        TODO("Step 5 - Delegate token refresh to authService and return ok response")
    }

    // TODO: Map to POST /logout with @ResponseStatus(HttpStatus.NO_CONTENT), accept validated RefreshTokenRequest in the request body, and return 204 No Content
    fun logout(request: RefreshTokenRequest): ResponseEntity<Unit> {
        TODO("Step 6 - Delegate token revocation to authService and return noContent response")
    }

    @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<UserResponse> {
        val response = authService.getCurrentUser()
        return ResponseEntity.ok(response)
    }
}
