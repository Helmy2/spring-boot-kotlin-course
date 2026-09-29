package com.example.shopcraft.product.dto

import com.example.shopcraft.product.entity.Product
import com.example.shopcraft.product.entity.ProductStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProductMappingTest {

    @Test
    fun `given product entity, when toResponse, then converts to matching response DTO`() {
        val entity = Product(
            id = 10L,
            sku = "SKU-TEST-001",
            name = "Test Item",
            description = "Description",
            price = BigDecimal("99.99"),
            stockQuantity = 20,
            status = ProductStatus.ACTIVE
        )

        val response = entity.toResponse()

        assertThat(response.id).isEqualTo(10L)
        assertThat(response.sku).isEqualTo("SKU-TEST-001")
        assertThat(response.name).isEqualTo("Test Item")
        assertThat(response.description).isEqualTo("Description")
        assertThat(response.price).isEqualByComparingTo("99.99")
        assertThat(response.stockQuantity).isEqualTo(20)
        assertThat(response.status).isEqualTo(ProductStatus.ACTIVE)
    }

    @Test
    fun `given product request, when toEntity, then converts to matching product entity`() {
        val request = ProductRequest(
            sku = "SKU-REQ-002",
            name = "Request Item",
            description = "Request Description",
            price = BigDecimal("49.99"),
            stockQuantity = 15,
            status = ProductStatus.ACTIVE
        )

        val entity = request.toEntity()

        assertThat(entity.id).isNull()
        assertThat(entity.sku).isEqualTo("SKU-REQ-002")
        assertThat(entity.name).isEqualTo("Request Item")
        assertThat(entity.description).isEqualTo("Request Description")
        assertThat(entity.price).isEqualByComparingTo("49.99")
        assertThat(entity.stockQuantity).isEqualTo(15)
        assertThat(entity.status).isEqualTo(ProductStatus.ACTIVE)
    }
}
