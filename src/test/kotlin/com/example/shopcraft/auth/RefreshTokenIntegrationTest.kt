package com.example.shopcraft.auth

import com.jayway.jsonpath.JsonPath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class RefreshTokenIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `given registered user, when login, then returns access token and refresh token`() {
        val registerPayload = """
            {
                "email": "refresh.test1@example.com",
                "password": "Password123!",
                "fullName": "Refresh Tester One"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerPayload)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.token").isString)
            .andExpect(jsonPath("$.refreshToken").isString)
            .andExpect(jsonPath("$.tokenType").value("Bearer"))

        val loginPayload = """
            {
                "email": "refresh.test1@example.com",
                "password": "Password123!"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginPayload)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").isString)
            .andExpect(jsonPath("$.refreshToken").isString)
    }

    @Test
    fun `given valid refresh token, when refresh, then rotates token and rejects old token reuse`() {
        val email = "refresh.rotate@example.com"
        val registerPayload = """
            {
                "email": "$email",
                "password": "Password123!",
                "fullName": "Rotate Tester"
            }
        """.trimIndent()

        val registerResult = mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerPayload)
        )
            .andExpect(status().isCreated)
            .andReturn()

        val initialRefreshToken: String = JsonPath.read(registerResult.response.contentAsString, "$.refreshToken")
        assertThat(initialRefreshToken).isNotBlank()

        val refreshPayload = """
            {
                "refreshToken": "$initialRefreshToken"
            }
        """.trimIndent()

        val refreshResult = mockMvc.perform(
            post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(refreshPayload)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").isString)
            .andExpect(jsonPath("$.refreshToken").isString)
            .andReturn()

        val rotatedRefreshToken: String = JsonPath.read(refreshResult.response.contentAsString, "$.refreshToken")
        assertThat(rotatedRefreshToken).isNotEqualTo(initialRefreshToken)

        val reusePayload = """
            {
                "refreshToken": "$initialRefreshToken"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reusePayload)
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.title").value("Token Refresh Failed"))
    }

    @Test
    fun `given active session, when logout, then refresh token is revoked and cannot be refreshed`() {
        val email = "logout.test@example.com"
        val registerPayload = """
            {
                "email": "$email",
                "password": "Password123!",
                "fullName": "Logout Tester"
            }
        """.trimIndent()

        val registerResult = mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerPayload)
        )
            .andExpect(status().isCreated)
            .andReturn()

        val refreshToken: String = JsonPath.read(registerResult.response.contentAsString, "$.refreshToken")

        val logoutPayload = """
            {
                "refreshToken": "$refreshToken"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(logoutPayload)
        )
            .andExpect(status().isNoContent)

        val refreshPayload = """
            {
                "refreshToken": "$refreshToken"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(refreshPayload)
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.title").value("Token Refresh Failed"))
    }
}
