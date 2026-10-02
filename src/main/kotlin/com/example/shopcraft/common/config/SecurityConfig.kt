package com.example.shopcraft.common.config

import com.example.shopcraft.auth.oauth2.CustomOAuth2UserService
import com.example.shopcraft.auth.oauth2.OAuth2AuthenticationSuccessHandler
import com.example.shopcraft.auth.oauth2.OAuth2Properties
import com.example.shopcraft.auth.security.CustomAccessDeniedHandler
import com.example.shopcraft.auth.security.JwtAuthenticationEntryPoint
import com.example.shopcraft.auth.security.JwtAuthenticationFilter
import com.example.shopcraft.auth.security.JwtProperties
import com.example.shopcraft.auth.security.JwtTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties::class, OAuth2Properties::class)
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

    @Autowired(required = false)
    private var customOAuth2UserService: CustomOAuth2UserService? = null

    @Autowired(required = false)
    private var oauth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler? = null

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager {
        return authConfig.authenticationManager
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .headers { headers -> headers.frameOptions { it.sameOrigin() } }
            .exceptionHandling { exceptions ->
                exceptions
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    .accessDeniedHandler(customAccessDeniedHandler)
            }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/", "/ui/**", "/favicon.ico").permitAll()
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/products/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/products/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/products/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**").hasRole("ADMIN")
                    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                    .requestMatchers("/h2-console/**").permitAll()
                    .requestMatchers("/error").permitAll()
                    .anyRequest().authenticated()
            }

        if (customOAuth2UserService != null && oauth2AuthenticationSuccessHandler != null) {
            http.oauth2Login { oauth2 ->
                oauth2
                    .userInfoEndpoint { it.userService(customOAuth2UserService) }
                    .successHandler(oauth2AuthenticationSuccessHandler)
            }
        }

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
