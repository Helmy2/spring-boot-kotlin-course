package com.example.shopcraft.auth.oauth2

import com.example.shopcraft.auth.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

class CustomOAuth2User(
    val user: User,
    private val attributes: Map<String, Any>
) : OAuth2User {

    override fun getName(): String = user.email

    override fun getAttributes(): Map<String, Any> = attributes

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority(user.role.name))
}
