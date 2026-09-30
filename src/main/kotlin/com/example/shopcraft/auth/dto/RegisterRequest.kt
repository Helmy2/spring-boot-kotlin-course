package com.example.shopcraft.auth.dto

import com.example.shopcraft.auth.entity.Role
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank(message = "Email must not be blank")
    @field:Email(message = "Email must be a valid email address")
    val email: String,

    @field:NotBlank(message = "Password must not be blank")
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    val password: String,

    @field:NotBlank(message = "Full name must not be blank")
    val fullName: String,

    val role: Role? = null
)
