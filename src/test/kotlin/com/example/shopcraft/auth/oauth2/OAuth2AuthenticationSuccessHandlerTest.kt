package com.example.shopcraft.auth.oauth2

import com.example.shopcraft.auth.entity.RefreshToken
import com.example.shopcraft.auth.entity.Role
import com.example.shopcraft.auth.entity.User
import com.example.shopcraft.auth.security.JwtTokenProvider
import com.example.shopcraft.auth.service.RefreshTokenService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.Authentication
import java.time.Instant

class OAuth2AuthenticationSuccessHandlerTest {

    private val jwtTokenProvider: JwtTokenProvider = mock()
    private val refreshTokenService: RefreshTokenService = mock()
    private val oAuth2Properties = OAuth2Properties(
        authorizedRedirectUri = "http://localhost:8080/api/v1/auth/oauth2/callback"
    )
    private val handler = OAuth2AuthenticationSuccessHandler(
        jwtTokenProvider,
        refreshTokenService,
        oAuth2Properties
    )

    private val testUser = User(
        id = 15L,
        email = "social.user@example.com",
        passwordHash = "social_hash",
        fullName = "Social Tester",
        role = Role.ROLE_USER,
        provider = "GITHUB"
    )

    @Test
    fun `given authenticated oauth2 principal, when onAuthenticationSuccess, then mints access token and refresh token and redirects with query params`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val authentication: Authentication = mock()
        val customOAuth2User = CustomOAuth2User(testUser, mapOf("id" to "15"))

        whenever(authentication.principal).thenReturn(customOAuth2User)
        whenever(jwtTokenProvider.generateToken(testUser.email, testUser.role)).thenReturn("oauth2.jwt.access.token")
        whenever(refreshTokenService.createRefreshToken(testUser)).thenReturn(
            RefreshToken(
                id = 1L,
                token = "oauth2-refresh-token-uuid",
                user = testUser,
                expiryDate = Instant.now().plusSeconds(604800)
            )
        )

        handler.onAuthenticationSuccess(request, response, authentication)

        val redirectedUrl = response.redirectedUrl
        assertThat(redirectedUrl).isNotNull
        assertThat(redirectedUrl).startsWith("http://localhost:8080/api/v1/auth/oauth2/callback")
        assertThat(redirectedUrl).contains("token=oauth2.jwt.access.token")
        assertThat(redirectedUrl).contains("refreshToken=oauth2-refresh-token-uuid")
        assertThat(redirectedUrl).contains("tokenType=Bearer")
    }

    @Test
    fun `given null authentication, when determineTargetUrl, then returns default authorized redirect uri`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()

        val url = handler.determineTargetUrl(request, response, null)

        assertThat(url).isEqualTo("http://localhost:8080/api/v1/auth/oauth2/callback")
    }
}
