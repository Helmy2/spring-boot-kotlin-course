package com.example.shopcraft.auth.service

import com.example.shopcraft.auth.dto.LoginRequest
import com.example.shopcraft.auth.dto.RegisterRequest
import com.example.shopcraft.auth.entity.Role
import com.example.shopcraft.auth.entity.User
import com.example.shopcraft.auth.repository.UserRepository
import com.example.shopcraft.auth.security.JwtTokenProvider
import com.example.shopcraft.common.exception.DuplicateResourceException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder

import com.example.shopcraft.auth.dto.RefreshTokenRequest
import com.example.shopcraft.auth.entity.RefreshToken
import com.example.shopcraft.auth.service.RefreshTokenService
import java.time.Instant

class AuthServiceTest {

    private val userRepository: UserRepository = mock()
    private val passwordEncoder: PasswordEncoder = mock()
    private val jwtTokenProvider: JwtTokenProvider = mock()
    private val refreshTokenService: RefreshTokenService = mock()
    private val authService: AuthService = AuthServiceImpl(
        userRepository,
        passwordEncoder,
        jwtTokenProvider,
        refreshTokenService
    )

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `given valid registration request, when register, then encodes password with bcrypt and persists user`() {
        val request = RegisterRequest(
            email = "newuser@example.com",
            password = "SecretPassword123!",
            fullName = "New User",
            role = Role.ROLE_USER
        )
        whenever(userRepository.existsByEmail("newuser@example.com")).thenReturn(false)
        whenever(passwordEncoder.encode("SecretPassword123!")).thenReturn("hashed_secret_password")
        whenever(userRepository.save(any<User>())).thenAnswer { invocation ->
            val entity = invocation.arguments[0] as User
            User(
                id = 10L,
                email = entity.email,
                passwordHash = entity.passwordHash,
                fullName = entity.fullName,
                role = entity.role
            )
        }
        whenever(jwtTokenProvider.generateToken("newuser@example.com", Role.ROLE_USER)).thenReturn("mocked.jwt.token")
        whenever(refreshTokenService.createRefreshToken(any())).thenAnswer { invocation ->
            val user = invocation.arguments[0] as User
            RefreshToken(id = 1L, token = "refresh.token.123", user = user, expiryDate = Instant.now().plusSeconds(3600))
        }

        val response = authService.register(request)

        assertThat(response.token).isEqualTo("mocked.jwt.token")
        assertThat(response.refreshToken).isEqualTo("refresh.token.123")
        assertThat(response.tokenType).isEqualTo("Bearer")
        assertThat(response.user.id).isEqualTo(10L)
        assertThat(response.user.email).isEqualTo("newuser@example.com")
        assertThat(response.user.role).isEqualTo(Role.ROLE_USER)
        verify(userRepository).save(any<User>())
    }

    @Test
    fun `given duplicate email, when register, then throws DuplicateResourceException`() {
        val request = RegisterRequest(
            email = "existing@example.com",
            password = "SecretPassword123!",
            fullName = "Existing User"
        )
        whenever(userRepository.existsByEmail("existing@example.com")).thenReturn(true)

        assertThatThrownBy {
            authService.register(request)
        }.isInstanceOf(DuplicateResourceException::class.java)
            .hasMessageContaining("User with email 'existing@example.com' already exists")

        verify(userRepository, never()).save(any())
    }

    @Test
    fun `given valid credentials, when login, then verifies password and returns token and user profile`() {
        val request = LoginRequest(
            email = "user@example.com",
            password = "CorrectPassword123!"
        )
        val user = User(
            id = 5L,
            email = "user@example.com",
            passwordHash = "encoded_hash",
            fullName = "Valid User",
            role = Role.ROLE_USER
        )
        whenever(userRepository.findByEmail("user@example.com")).thenReturn(user)
        whenever(passwordEncoder.matches("CorrectPassword123!", "encoded_hash")).thenReturn(true)
        whenever(jwtTokenProvider.generateToken("user@example.com", Role.ROLE_USER)).thenReturn("valid.login.jwt")
        whenever(refreshTokenService.createRefreshToken(user)).thenReturn(
            RefreshToken(id = 2L, token = "refresh.token.login", user = user, expiryDate = Instant.now().plusSeconds(3600))
        )

        val response = authService.login(request)

        assertThat(response.token).isEqualTo("valid.login.jwt")
        assertThat(response.refreshToken).isEqualTo("refresh.token.login")
        assertThat(response.user.email).isEqualTo("user@example.com")
        assertThat(response.user.fullName).isEqualTo("Valid User")
    }

    @Test
    fun `given valid refresh token request, when refreshToken, then rotates token and returns new auth response`() {
        val user = User(
            id = 7L,
            email = "refreshed@example.com",
            passwordHash = "hash",
            fullName = "Refresh User",
            role = Role.ROLE_USER
        )
        val newRefreshToken = RefreshToken(
            id = 3L,
            token = "new-rotated-refresh-token",
            user = user,
            expiryDate = Instant.now().plusSeconds(3600)
        )
        whenever(refreshTokenService.rotateRefreshToken("old-refresh-token")).thenReturn(Pair(newRefreshToken, user))
        whenever(jwtTokenProvider.generateToken("refreshed@example.com", Role.ROLE_USER)).thenReturn("new.access.token")

        val response = authService.refreshToken(RefreshTokenRequest("old-refresh-token"))

        assertThat(response.token).isEqualTo("new.access.token")
        assertThat(response.refreshToken).isEqualTo("new-rotated-refresh-token")
        assertThat(response.user.email).isEqualTo("refreshed@example.com")
        verify(refreshTokenService).rotateRefreshToken("old-refresh-token")
    }

    @Test
    fun `given valid logout request, when logout, then delegates revocation to refreshTokenService`() {
        val request = RefreshTokenRequest("logout-refresh-token")

        authService.logout(request)

        verify(refreshTokenService).revokeRefreshToken("logout-refresh-token")
    }

    @Test
    fun `given incorrect password, when login, then throws BadCredentialsException`() {
        val request = LoginRequest(
            email = "user@example.com",
            password = "WrongPassword!"
        )
        val user = User(
            id = 5L,
            email = "user@example.com",
            passwordHash = "encoded_hash",
            fullName = "Valid User",
            role = Role.ROLE_USER
        )
        whenever(userRepository.findByEmail("user@example.com")).thenReturn(user)
        whenever(passwordEncoder.matches("WrongPassword!", "encoded_hash")).thenReturn(false)

        assertThatThrownBy {
            authService.login(request)
        }.isInstanceOf(BadCredentialsException::class.java)
            .hasMessageContaining("Invalid email or password")
    }

    @Test
    fun `given authenticated security context, when getCurrentUser, then returns matching user response`() {
        val email = "authenticated@example.com"
        val auth = UsernamePasswordAuthenticationToken(email, null, emptyList())
        SecurityContextHolder.getContext().authentication = auth

        val user = User(
            id = 42L,
            email = email,
            passwordHash = "hash",
            fullName = "Authenticated Person",
            role = Role.ROLE_USER
        )
        whenever(userRepository.findByEmail(email)).thenReturn(user)

        val profile = authService.getCurrentUser()

        assertThat(profile.id).isEqualTo(42L)
        assertThat(profile.email).isEqualTo(email)
        assertThat(profile.fullName).isEqualTo("Authenticated Person")
    }
}
