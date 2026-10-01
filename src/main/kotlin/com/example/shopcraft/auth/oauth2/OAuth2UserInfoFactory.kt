package com.example.shopcraft.auth.oauth2

import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2Error

object OAuth2UserInfoFactory {
    fun getOAuth2UserInfo(registrationId: String, attributes: Map<String, Any>): OAuth2UserInfo {
        return when (registrationId.lowercase()) {
            "github" -> GitHubOAuth2UserInfo(attributes)
            else -> {
                val error = OAuth2Error("unsupported_provider", "Login with provider '$registrationId' is not supported", null)
                throw OAuth2AuthenticationException(error, error.description)
            }
        }
    }
}
