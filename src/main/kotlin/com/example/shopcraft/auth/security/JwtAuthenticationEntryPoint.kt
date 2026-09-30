package com.example.shopcraft.auth.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        val timestamp = Instant.now().toString()
        val path = request.requestURI
        val detail = authException.message ?: "Authentication required to access this resource"

        val jsonBody = """
            {
                "type": "https://api.shopcraft.com/errors/unauthorized",
                "title": "Unauthorized",
                "status": 401,
                "detail": "$detail",
                "timestamp": "$timestamp",
                "path": "$path"
            }
        """.trimIndent()

        response.writer.write(jsonBody)
    }
}
