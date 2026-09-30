package com.example.shopcraft.testing.support

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.postgresql.PostgreSQLContainer

@TestConfiguration(proxyBeanMethods = false)
class PostgreSqlContainerConfig {

    @Bean
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer {
        TODO("Step 6 - Configure and return a PostgreSQLContainer using postgres:17-alpine with database name shopcraft_test, username shopcraft, and password secret.")
    }
}
