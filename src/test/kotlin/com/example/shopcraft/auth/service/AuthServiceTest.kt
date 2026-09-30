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

class AuthServiceTest {

    private val userRepository: UserRepository = mock()
    private val passwordEncoder: PasswordEncoder = mock()
    private val jwtTokenProvider: JwtTokenProvider = mock()
    private val authService: AuthService = AuthServiceImpl(userRepository, passwordEncoder, jwtTokenProvider)

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

        val response = authService.register(request)

        assertThat(response.token).isEqualTo("mocked.jwt.token")
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

        val response = authService.login(request)

        assertThat(response.token).isEqualTo("valid.login.jwt")
        assertThat(response.user.email).isEqualTo("user@example.com")
        assertThat(response.user.fullName).isEqualTo("Valid User")
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
