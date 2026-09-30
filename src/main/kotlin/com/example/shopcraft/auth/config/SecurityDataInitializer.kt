package com.example.shopcraft.auth.config

import com.example.shopcraft.auth.entity.Role
import com.example.shopcraft.auth.entity.User
import com.example.shopcraft.auth.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class SecurityDataInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String) {
        if (!userRepository.existsByEmail("admin@shopcraft.com")) {
            userRepository.save(
                User(
                    email = "admin@shopcraft.com",
                    passwordHash = passwordEncoder.encode("Admin123!") ?: "",
                    fullName = "ShopCraft Administrator",
                    role = Role.ROLE_ADMIN
                )
            )
        }

        if (!userRepository.existsByEmail("customer@shopcraft.com")) {
            userRepository.save(
                User(
                    email = "customer@shopcraft.com",
                    passwordHash = passwordEncoder.encode("Customer123!") ?: "",
                    fullName = "Standard Customer",
                    role = Role.ROLE_USER
                )
            )
        }
    }
}
