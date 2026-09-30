package com.example.shopcraft.common.config

import com.example.shopcraft.auth.security.CustomAccessDeniedHandler
import com.example.shopcraft.auth.security.JwtAuthenticationEntryPoint
import com.example.shopcraft.auth.security.JwtAuthenticationFilter
import com.example.shopcraft.auth.security.JwtProperties
import com.example.shopcraft.auth.security.JwtTokenProvider
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties::class)
@Import(
    JwtAuthenticationFilter::class,
    JwtAuthenticationEntryPoint::class,
    CustomAccessDeniedHandler::class,
    JwtTokenProvider::class
)
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val customAccessDeniedHandler: CustomAccessDeniedHandler
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager {
        return authConfig.authenticationManager
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        // Step 7 - Configure the security filter chain:
        // 1. Disable CSRF for stateless REST.
        // 2. Set SessionCreationPolicy to STATELESS.
        // 3. Register jwtAuthenticationEntryPoint and customAccessDeniedHandler in exceptionHandling.
        // 4. In authorizeHttpRequests: permit /api/v1/auth/**, permit GET on /api/v1/products/**,
        //    restrict POST/PUT/PATCH/DELETE on /api/v1/products/** to ROLE_ADMIN,
        //    restrict /api/v1/admin/** to ROLE_ADMIN, permit /h2-console/** and /error, require authentication for anyRequest.
        // 5. Add jwtAuthenticationFilter before UsernamePasswordAuthenticationFilter.
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .headers { headers -> headers.frameOptions { it.sameOrigin() } }
            .authorizeHttpRequests { auth ->
                auth.anyRequest().permitAll()
            }

        return http.build()
    }
}
