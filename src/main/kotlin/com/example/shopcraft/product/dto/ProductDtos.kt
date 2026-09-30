package com.example.shopcraft.product.dto

import com.example.shopcraft.product.entity.Product
import com.example.shopcraft.product.entity.ProductStatus
import com.example.shopcraft.product.validation.ValidSku
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.Instant

data class ProductRequest(
    @field:NotBlank(message = "SKU must not be blank")
    @field:ValidSku
    val sku: String,

    @field:NotBlank(message = "Name must not be blank")
    @field:Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    val name: String,

    @field:Size(max = 1000, message = "Description must not exceed 1000 characters")
    val description: String? = null,

    @field:NotNull(message = "Price is required")
    @field:Positive(message = "Price must be strictly positive")
    val price: BigDecimal,

    @field:NotNull(message = "Stock quantity is required")
    @field:Min(value = 0, message = "Stock quantity must be zero or positive")
    val stockQuantity: Int,

    @field:NotNull(message = "Status is required")
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
    @field:Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    val name: String? = null,

    @field:Size(max = 1000, message = "Description must not exceed 1000 characters")
    val description: String? = null,

    @field:Positive(message = "Price must be strictly positive")
    val price: BigDecimal? = null,

    @field:Min(value = 0, message = "Stock quantity must be zero or positive")
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
