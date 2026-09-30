package com.example.shopcraft.testing.integration

import com.example.shopcraft.audit.repository.AuditEventRepository
import com.example.shopcraft.product.repository.ProductRepository
import com.example.shopcraft.testing.support.EnabledIfDockerAvailable
import com.example.shopcraft.testing.support.PostgreSqlContainerConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

import org.springframework.security.test.context.support.WithMockUser

@SpringBootTest
@AutoConfigureMockMvc
@Import(PostgreSqlContainerConfig::class)
@EnabledIfDockerAvailable
@WithMockUser(roles = ["ADMIN"])
class ProductEndToEndIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var auditEventRepository: AuditEventRepository

    @Test
    fun `given valid product request, when POST api v1 products, then persists in real postgresql and records audit event in database`() {
        val payload = """
            {
                "sku": "SKU-E2E-0001",
                "name": "Testcontainers Mechanical Keyboard",
                "description": "End-to-end integration test with real database",
                "price": 199.99,
                "stockQuantity": 40,
                "status": "ACTIVE"
            }
        """.trimIndent()

        val result = mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        )
            .andExpect(status().isCreated)
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.sku").value("SKU-E2E-0001"))
            .andExpect(jsonPath("$.name").value("Testcontainers Mechanical Keyboard"))
            .andReturn()

        val location = result.response.getHeader("Location") ?: ""
        val createdId = location.substringAfterLast("/").toLong()
        val savedProduct = productRepository.findById(createdId)
        assertThat(savedProduct).isPresent
        assertThat(savedProduct.get().sku).isEqualTo("SKU-E2E-0001")

        val auditEvents = auditEventRepository.findAll()
        val matchingEvent = auditEvents.find { it.resourceId == createdId.toString() }
        assertThat(matchingEvent).isNotNull
        assertThat(matchingEvent?.action).isEqualTo("PRODUCT_CREATED")
        assertThat(matchingEvent?.resourceType).isEqualTo("PRODUCT")
    }
}
