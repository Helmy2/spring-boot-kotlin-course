package com.example.shopcraft.auth.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtProperties: JwtProperties
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader(jwtProperties.headerString)
        if (authHeader != null && authHeader.startsWith(jwtProperties.tokenPrefix)) {
            TODO("Step 4 - Extract the Bearer token, validate it with jwtTokenProvider, extract username and authorities, and populate the SecurityContextHolder with an authenticated UsernamePasswordAuthenticationToken before continuing the filter chain.")
        }

        filterChain.doFilter(request, response)
    }
}
