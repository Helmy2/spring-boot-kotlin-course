package com.example.shopcraft.auth.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class CustomAccessDeniedHandler : AccessDeniedHandler {

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        val timestamp = Instant.now().toString()
        val path = request.requestURI

        val jsonBody = """
            {
                "type": "https://api.shopcraft.com/errors/forbidden",
                "title": "Access Denied",
                "status": 403,
                "detail": "Access denied: you do not have permission to access this resource",
                "timestamp": "$timestamp",
                "path": "$path"
            }
        """.trimIndent()

        response.writer.write(jsonBody)
    }
}
