package com.example.shopcraft.auth.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
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
            val token = authHeader.removePrefix(jwtProperties.tokenPrefix).trim()
            if (jwtTokenProvider.validateToken(token)) {
                val username = jwtTokenProvider.extractUsername(token)
                val roles = jwtTokenProvider.extractRoles(token)
                val authorities = roles.map { SimpleGrantedAuthority(it) }

                val authentication = UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        filterChain.doFilter(request, response)
    }
}
