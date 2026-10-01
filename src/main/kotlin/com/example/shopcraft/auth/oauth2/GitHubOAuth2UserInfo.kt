package com.example.shopcraft.auth.oauth2

class GitHubOAuth2UserInfo(
    attributes: Map<String, Any>
) : OAuth2UserInfo(attributes) {

    override val id: String
        get() = attributes["id"]?.toString() ?: ""

    override val name: String?
        get() = attributes["name"] as? String ?: attributes["login"] as? String ?: "GitHub User"

    override val email: String?
        get() = attributes["email"] as? String
            ?: "${attributes["login"] ?: id}@users.noreply.github.com"

    override val imageUrl: String?
        get() = attributes["avatar_url"] as? String
}
