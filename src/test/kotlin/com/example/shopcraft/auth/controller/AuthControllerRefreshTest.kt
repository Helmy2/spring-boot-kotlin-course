package com.example.shopcraft.auth.controller

import com.example.shopcraft.auth.dto.AuthResponse
import com.example.shopcraft.auth.dto.RefreshTokenRequest
import com.example.shopcraft.auth.dto.UserResponse
import com.example.shopcraft.auth.entity.Role
import com.example.shopcraft.auth.service.AuthService
import com.example.shopcraft.common.config.SecurityConfig
import com.example.shopcraft.common.exception.GlobalExceptionHandler
import com.example.shopcraft.common.exception.TokenRefreshException
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant

@WebMvcTest(AuthController::class)
@Import(SecurityConfig::class, GlobalExceptionHandler::class)
class AuthControllerRefreshTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var authService: AuthService

    @Test
    fun `given valid refresh token, when POST refresh, then returns 200 OK with new tokens`() {
        val authResponse = AuthResponse(
            token = "new.jwt.access.token",
            refreshToken = "new-uuid-refresh-token",
            tokenType = "Bearer",
            user = UserResponse(
                id = 1L,
                email = "user@example.com",
                fullName = "Sample User",
                role = Role.ROLE_USER,
                createdAt = Instant.now()
            )
        )
        whenever(authService.refreshToken(any<RefreshTokenRequest>())).thenReturn(authResponse)

        val payload = """
            {
                "refreshToken": "valid-old-refresh-token"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").value("new.jwt.access.token"))
            .andExpect(jsonPath("$.refreshToken").value("new-uuid-refresh-token"))
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.user.email").value("user@example.com"))
    }

    @Test
    fun `given blank refresh token, when POST refresh, then returns 400 Bad Request`() {
        val payload = """
            {
                "refreshToken": "   "
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.errors[?(@.field == 'refreshToken')]").exists())
    }

    @Test
    fun `given invalid or expired refresh token, when POST refresh, then returns 401 Unauthorized ProblemDetail`() {
        whenever(authService.refreshToken(any<RefreshTokenRequest>()))
            .thenThrow(TokenRefreshException("bad-token", "Refresh token has expired. Please sign in again."))

        val payload = """
            {
                "refreshToken": "bad-token"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.title").value("Token Refresh Failed"))
            .andExpect(jsonPath("$.detail").value("Failed for [bad-token]: Refresh token has expired. Please sign in again."))
    }

    @Test
    fun `given valid refresh token, when POST logout, then returns 204 No Content`() {
        doNothing().whenever(authService).logout(any<RefreshTokenRequest>())

        val payload = """
            {
                "refreshToken": "token-to-logout"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        )
            .andExpect(status().isNoContent)
    }
}
