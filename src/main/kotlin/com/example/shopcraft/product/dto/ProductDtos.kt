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
    fun toEntity(): Product = Product(
        sku = sku,
        name = name,
        description = description,
        price = price,
        stockQuantity = stockQuantity,
        status = status
    )
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

data class ProductPatchRequest(
    val name: String? = null,
    val description: String? = null,
    val price: BigDecimal? = null,
    val stockQuantity: Int? = null,
    val status: ProductStatus? = null
)

data class ProductFilterCriteria(
    val search: String? = null,
    val minPrice: BigDecimal? = null,
    val maxPrice: BigDecimal? = null,
    val status: ProductStatus? = null,
    val inStock: Boolean? = null
)
