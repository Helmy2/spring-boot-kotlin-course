package com.example.shopcraft.testing.support

import com.example.shopcraft.product.dto.ProductRequest
import com.example.shopcraft.product.dto.ProductResponse
import com.example.shopcraft.product.entity.Product
import com.example.shopcraft.product.entity.ProductStatus
import java.math.BigDecimal
import java.time.Instant

object ProductTestDataFactory {

    fun createProductRequest(
        sku: String = "SKU-TST-1001",
        name: String = "Test Keyboard",
        description: String? = "Test description",
        price: BigDecimal = BigDecimal("99.99"),
        stockQuantity: Int = 20,
        status: ProductStatus = ProductStatus.ACTIVE
    ): ProductRequest = ProductRequest(
        sku = sku,
        name = name,
        description = description,
        price = price,
        stockQuantity = stockQuantity,
        status = status
    )

    fun createProductEntity(
        id: Long? = 1L,
        sku: String = "SKU-TST-1001",
        name: String = "Test Keyboard",
        description: String? = "Test description",
        price: BigDecimal = BigDecimal("99.99"),
        stockQuantity: Int = 20,
        status: ProductStatus = ProductStatus.ACTIVE
    ): Product = Product(
        id = id,
        sku = sku,
        name = name,
        description = description,
        price = price,
        stockQuantity = stockQuantity,
        status = status
    )

    fun createProductResponse(
        id: Long = 1L,
        sku: String = "SKU-TST-1001",
        name: String = "Test Keyboard",
        description: String? = "Test description",
        price: BigDecimal = BigDecimal("99.99"),
        stockQuantity: Int = 20,
        status: ProductStatus = ProductStatus.ACTIVE,
        createdAt: Instant = Instant.now(),
        updatedAt: Instant = Instant.now()
    ): ProductResponse = ProductResponse(
        id = id,
        sku = sku,
        name = name,
        description = description,
        price = price,
        stockQuantity = stockQuantity,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
