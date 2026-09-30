package com.example.shopcraft.auth.controller

import com.example.shopcraft.auth.dto.AuthResponse
import com.example.shopcraft.auth.dto.LoginRequest
import com.example.shopcraft.auth.dto.RegisterRequest
import com.example.shopcraft.auth.dto.UserResponse
import com.example.shopcraft.auth.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    // Step 8a - Annotate this method with @PostMapping("/register") and @ResponseStatus(HttpStatus.CREATED)
    // Annotate the request parameter with @Valid and @RequestBody
    fun register(request: RegisterRequest): ResponseEntity<AuthResponse> {
        TODO("Step 8a - Delegate user registration to authService and return HTTP 201 Created with the authentication response.")
    }

    // Step 8b - Annotate this method with @PostMapping("/login")
    // Annotate the request parameter with @Valid and @RequestBody
    fun login(request: LoginRequest): ResponseEntity<AuthResponse> {
        TODO("Step 8b - Delegate user authentication to authService and return HTTP 200 OK with the authentication response.")
    }

    // Step 8c - Annotate this method with @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<UserResponse> {
        TODO("Step 8c - Retrieve the authenticated user profile from authService and return HTTP 200 OK.")
    }
}
