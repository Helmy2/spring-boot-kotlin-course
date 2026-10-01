package com.example.shopcraft.auth.oauth2

import com.example.shopcraft.auth.security.JwtTokenProvider
import com.example.shopcraft.auth.service.RefreshTokenService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

@Component
class OAuth2AuthenticationSuccessHandler(
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshTokenService: RefreshTokenService,
    private val oAuth2Properties: OAuth2Properties
) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val targetUrl = determineTargetUrl(request, response, authentication)

        if (response.isCommitted) {
            logger.debug("Response has already been committed. Unable to redirect to $targetUrl")
            return
        }

        clearAuthenticationAttributes(request)
        redirectStrategy.sendRedirect(request, response, targetUrl)
    }

    public override fun determineTargetUrl(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ): String {
        val oAuth2User = (authentication?.principal as? CustomOAuth2User)
            ?: return oAuth2Properties.authorizedRedirectUri
        val user = oAuth2User.user

        val accessToken = jwtTokenProvider.generateToken(user.email, user.role)
        val refreshToken = refreshTokenService.createRefreshToken(user)

        return UriComponentsBuilder.fromUriString(oAuth2Properties.authorizedRedirectUri)
            .queryParam("token", accessToken)
            .queryParam("refreshToken", refreshToken.token)
            .queryParam("tokenType", "Bearer")
            .build()
            .toUriString()
    }
}
