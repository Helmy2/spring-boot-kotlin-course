package com.example.shopcraft.testing.integration

import com.example.shopcraft.audit.repository.AuditEventRepository
import com.example.shopcraft.product.repository.ProductRepository
import com.example.shopcraft.testing.support.EnabledIfDockerAvailable
import com.example.shopcraft.testing.support.PostgreSqlContainerConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@AutoConfigureMockMvc
@Import(PostgreSqlContainerConfig::class)
@EnabledIfDockerAvailable
class ProductEndToEndIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var auditEventRepository: AuditEventRepository

    @Test
    fun `given valid product request, when POST api v1 products, then persists in real postgresql and records audit event in database`() {
        TODO("Step 7 - Perform a POST request to /api/v1/products with a valid product payload. Assert HTTP 201 Created and Location header, verify the product was persisted into the real PostgreSQL database via productRepository, and verify that the AOP audit event PRODUCT_CREATED was recorded in auditEventRepository.")
    }
}
