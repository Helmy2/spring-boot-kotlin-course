package com.example.shopcraft.auth.oauth2

import com.example.shopcraft.auth.entity.Role
import com.example.shopcraft.auth.entity.User
import com.example.shopcraft.auth.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import java.time.Instant

class CustomOAuth2UserServiceTest {

    private val userRepository: UserRepository = mock()
    private val customOAuth2UserService = CustomOAuth2UserService(userRepository)

    private val githubRegistration = ClientRegistration.withRegistrationId("github")
        .clientId("dummy-client-id")
        .clientSecret("dummy-client-secret")
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
        .authorizationUri("https://github.com/login/oauth/authorize")
        .tokenUri("https://github.com/login/oauth/access_token")
        .userInfoUri("https://api.github.com/user")
        .userNameAttributeName("id")
        .build()

    private val accessToken = OAuth2AccessToken(
        OAuth2AccessToken.TokenType.BEARER,
        "dummy-access-token",
        Instant.now(),
        Instant.now().plusSeconds(3600)
    )

    private val userRequest = OAuth2UserRequest(githubRegistration, accessToken)

    @Test
    fun `given new github user info, when processOAuth2User, then auto-provisions user in repository and returns CustomOAuth2User`() {
        val oAuth2User: OAuth2User = mock()
        val attributes = mapOf<String, Any>(
            "id" to 12345,
            "login" to "octocat",
            "name" to "Monalisa Octocat",
            "email" to "octocat@github.com",
            "avatar_url" to "https://avatars.githubusercontent.com/u/12345"
        )
        whenever(oAuth2User.attributes).thenReturn(attributes)
        whenever(userRepository.findByEmail("octocat@github.com")).thenReturn(null)
        whenever(userRepository.save(any<User>())).thenAnswer { invocation ->
            val user = invocation.arguments[0] as User
            User(
                id = 100L,
                email = user.email,
                passwordHash = user.passwordHash,
                fullName = user.fullName,
                role = user.role,
                provider = user.provider
            )
        }

        val result = customOAuth2UserService.processOAuth2User(userRequest, oAuth2User)

        assertThat(result).isInstanceOf(CustomOAuth2User::class.java)
        val customUser = result as CustomOAuth2User
        assertThat(customUser.user.email).isEqualTo("octocat@github.com")
        assertThat(customUser.user.fullName).isEqualTo("Monalisa Octocat")
        assertThat(customUser.user.role).isEqualTo(Role.ROLE_USER)
        assertThat(customUser.user.provider).isEqualTo("GITHUB")
        assertThat(customUser.authorities.map { it.authority }).containsExactly("ROLE_USER")
        verify(userRepository).save(any<User>())
    }

    @Test
    fun `given existing user with same email, when processOAuth2User, then updates name and returns existing user without duplicate creation`() {
        val existingUser = User(
            id = 42L,
            email = "octocat@github.com",
            passwordHash = "existing_hash",
            fullName = "Old Name",
            role = Role.ROLE_USER,
            provider = "LOCAL"
        )
        val oAuth2User: OAuth2User = mock()
        val attributes = mapOf<String, Any>(
            "id" to 12345,
            "login" to "octocat",
            "name" to "New Monalisa Name",
            "email" to "octocat@github.com"
        )
        whenever(oAuth2User.attributes).thenReturn(attributes)
        whenever(userRepository.findByEmail("octocat@github.com")).thenReturn(existingUser)
        whenever(userRepository.save(any<User>())).thenAnswer { it.arguments[0] as User }

        val result = customOAuth2UserService.processOAuth2User(userRequest, oAuth2User)

        val customUser = result as CustomOAuth2User
        assertThat(customUser.user.id).isEqualTo(42L)
        assertThat(customUser.user.fullName).isEqualTo("New Monalisa Name")
        verify(userRepository).save(existingUser)
    }

    @Test
    fun `given missing email in attributes, when processOAuth2User, then falls back to noreply github email and auto-provisions`() {
        val oAuth2User: OAuth2User = mock()
        val attributes = mapOf<String, Any>(
            "id" to 99999,
            "login" to "private_email_user",
            "name" to "Private User"
        )
        whenever(oAuth2User.attributes).thenReturn(attributes)
        whenever(userRepository.findByEmail("private_email_user@users.noreply.github.com")).thenReturn(null)
        whenever(userRepository.save(any<User>())).thenAnswer { it.arguments[0] as User }

        val result = customOAuth2UserService.processOAuth2User(userRequest, oAuth2User)

        val customUser = result as CustomOAuth2User
        assertThat(customUser.user.email).isEqualTo("private_email_user@users.noreply.github.com")
        assertThat(customUser.user.fullName).isEqualTo("Private User")
        verify(userRepository).save(any<User>())
    }

    @Test
    fun `given unsupported provider registration id, when processOAuth2User, then throws OAuth2AuthenticationException`() {
        val unsupportedRegistration = ClientRegistration.withRegistrationId("facebook")
            .clientId("dummy")
            .clientSecret("secret")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .authorizationUri("https://facebook.com/dialog/oauth")
            .tokenUri("https://graph.facebook.com/v12.0/oauth/access_token")
            .userInfoUri("https://graph.facebook.com/me")
            .userNameAttributeName("id")
            .build()
        val unsupportedRequest = OAuth2UserRequest(unsupportedRegistration, accessToken)
        val oAuth2User: OAuth2User = mock()
        whenever(oAuth2User.attributes).thenReturn(mapOf("id" to "123"))

        assertThatThrownBy {
            customOAuth2UserService.processOAuth2User(unsupportedRequest, oAuth2User)
        }.isInstanceOf(OAuth2AuthenticationException::class.java)
            .hasMessageContaining("Login with provider 'facebook' is not supported")

        verify(userRepository, never()).save(any())
    }
}
