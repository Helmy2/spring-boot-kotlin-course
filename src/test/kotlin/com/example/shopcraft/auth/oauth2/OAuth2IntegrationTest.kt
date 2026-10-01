package com.example.shopcraft.auth.oauth2

import com.example.shopcraft.auth.entity.Role
import com.example.shopcraft.auth.entity.User
import com.example.shopcraft.auth.repository.UserRepository
import com.example.shopcraft.auth.security.JwtTokenProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class OAuth2IntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Test
    fun `given valid tokens, when GET oauth2 callback endpoint, then returns 200 OK with bearer tokens`() {
        mockMvc.perform(
            get("/api/v1/auth/oauth2/callback")
                .param("token", "mock-jwt-token")
                .param("refreshToken", "mock-refresh-token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").value("mock-jwt-token"))
            .andExpect(jsonPath("$.refreshToken").value("mock-refresh-token"))
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun `given auto-provisioned oauth2 user, when accessing protected endpoint with jwt, then succeeds with 200 OK`() {
        val email = "github.oauth2.test@example.com"
        val user = userRepository.save(
            User(
                email = email,
                passwordHash = "oauth2_dummy_hash",
                fullName = "GitHub OAuth2 Tester",
                role = Role.ROLE_USER,
                provider = "GITHUB"
            )
        )
        val token = jwtTokenProvider.generateToken(user.email, user.role)

        mockMvc.perform(
            get("/api/v1/auth/me")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.fullName").value("GitHub OAuth2 Tester"))
            .andExpect(jsonPath("$.role").value("ROLE_USER"))
    }

    @Test
    fun `given oauth2 authorization request, when GET authorization github, then redirects to github oauth2`() {
        val result = mockMvc.perform(
            get("/oauth2/authorization/github")
        )
            .andExpect(status().is3xxRedirection)
            .andReturn()

        val redirectedUrl = result.response.redirectedUrl
        assertThat(redirectedUrl).isNotNull
        assertThat(redirectedUrl).contains("github.com/login/oauth/authorize")
    }
}
