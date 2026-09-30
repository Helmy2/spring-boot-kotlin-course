package com.example.shopcraft.auth.controller

import com.example.shopcraft.auth.dto.AuthResponse
import com.example.shopcraft.auth.dto.LoginRequest
import com.example.shopcraft.auth.dto.RegisterRequest
import com.example.shopcraft.auth.dto.UserResponse
import com.example.shopcraft.auth.entity.Role
import com.example.shopcraft.auth.service.AuthService
import com.example.shopcraft.common.config.SecurityConfig
import com.example.shopcraft.common.exception.GlobalExceptionHandler
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant

@WebMvcTest(AuthController::class)
@Import(SecurityConfig::class, GlobalExceptionHandler::class)
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var authService: AuthService

    @Test
    fun `given valid registration request, when POST register, then returns 201 Created and auth response`() {
        val authResponse = AuthResponse(
            token = "jwt.token.register",
            tokenType = "Bearer",
            user = UserResponse(
                id = 1L,
                email = "alice@example.com",
                fullName = "Alice Cooper",
                role = Role.ROLE_USER,
                createdAt = Instant.now()
            )
        )
        whenever(authService.register(any<RegisterRequest>())).thenReturn(authResponse)

        val payload = """
            {
                "email": "alice@example.com",
                "password": "Password123!",
                "fullName": "Alice Cooper"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.token").value("jwt.token.register"))
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.user.email").value("alice@example.com"))
    }

    @Test
    fun `given invalid email, when POST register, then returns 400 Bad Request with field error`() {
        val payload = """
            {
                "email": "not-an-email",
                "password": "Password123!",
                "fullName": "Alice Cooper"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.errors[?(@.field == 'email')]").exists())
    }

    @Test
    fun `given valid credentials, when POST login, then returns 200 OK and bearer token`() {
        val authResponse = AuthResponse(
            token = "jwt.token.login",
            tokenType = "Bearer",
            user = UserResponse(
                id = 2L,
                email = "bob@example.com",
                fullName = "Bob Ross",
                role = Role.ROLE_USER,
                createdAt = Instant.now()
            )
        )
        whenever(authService.login(any<LoginRequest>())).thenReturn(authResponse)

        val payload = """
            {
                "email": "bob@example.com",
                "password": "HappyTrees123!"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").value("jwt.token.login"))
            .andExpect(jsonPath("$.user.fullName").value("Bob Ross"))
    }

    @Test
    @WithMockUser(username = "charlie@example.com")
    fun `given authenticated user, when GET me, then returns 200 OK and user profile`() {
        val userResponse = UserResponse(
            id = 3L,
            email = "charlie@example.com",
            fullName = "Charlie Brown",
            role = Role.ROLE_USER,
            createdAt = Instant.now()
        )
        whenever(authService.getCurrentUser()).thenReturn(userResponse)

        mockMvc.perform(get("/api/v1/auth/me"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(3))
            .andExpect(jsonPath("$.email").value("charlie@example.com"))
            .andExpect(jsonPath("$.fullName").value("Charlie Brown"))
    }
}
