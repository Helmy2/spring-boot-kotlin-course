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
import org.springframework.web.bind.annotation.RequestParam
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

    @PostMapping("/refresh")
    fun refreshToken(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<AuthResponse> {
        val response = authService.refreshToken(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<Unit> {
        authService.logout(request)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/oauth2/callback")
    fun oauth2Callback(
        @RequestParam token: String,
        @RequestParam refreshToken: String
    ): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(
            mapOf(
                "token" to token,
                "refreshToken" to refreshToken,
                "tokenType" to "Bearer",
                "message" to "OAuth2 authentication successful. Bearer tokens minted."
            )
        )
    }

    @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<UserResponse> {
        val response = authService.getCurrentUser()
        return ResponseEntity.ok(response)
    }
}
