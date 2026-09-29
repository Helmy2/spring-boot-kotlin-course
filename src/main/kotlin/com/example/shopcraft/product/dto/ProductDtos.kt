package com.example.shopcraft.product.dto

import com.example.shopcraft.product.entity.Product
import com.example.shopcraft.product.entity.ProductStatus
import java.math.BigDecimal
import java.time.Instant

data class ProductRequest(
    val sku: String,
    val name: String,
    val description: String? = null,
    val price: BigDecimal,
    val stockQuantity: Int,
    val status: ProductStatus = ProductStatus.ACTIVE
) {
    fun toEntity(): Product {
        TODO("Step 3 - Construct and return a new Product entity from this request attributes")
    }
}

data class ProductResponse(
    val id: Long,
    val sku: String,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val stockQuantity: Int,
    val status: ProductStatus,
    val createdAt: Instant,
    val updatedAt: Instant
)
