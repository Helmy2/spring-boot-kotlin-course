package com.example.shopcraft.auth.oauth2

import com.example.shopcraft.auth.entity.Role
import com.example.shopcraft.auth.entity.User
import com.example.shopcraft.auth.repository.UserRepository
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository
) : DefaultOAuth2UserService() {

    @Transactional
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        return processOAuth2User(userRequest, oAuth2User)
    }

    fun processOAuth2User(userRequest: OAuth2UserRequest, oAuth2User: OAuth2User): OAuth2User {
        val registrationId = userRequest.clientRegistration.registrationId
        val userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.attributes)

        val email = userInfo.email?.trim()?.lowercase()
        if (email.isNullOrBlank()) {
            val error = org.springframework.security.oauth2.core.OAuth2Error("invalid_email", "Email not found from OAuth2 provider '$registrationId'", null)
            throw OAuth2AuthenticationException(error, error.description)
        }

        val user = userRepository.findByEmail(email)?.let { existingUser ->
            userInfo.name?.let { updatedName ->
                existingUser.fullName = updatedName
            }
            userRepository.save(existingUser)
        } ?: run {
            val newUser = User(
                email = email,
                passwordHash = UUID.randomUUID().toString(),
                fullName = userInfo.name ?: "OAuth2 User",
                role = Role.ROLE_USER,
                provider = registrationId.uppercase()
            )
            userRepository.save(newUser)
        }

        return CustomOAuth2User(user, oAuth2User.attributes)
    }
}
