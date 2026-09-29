package com.example.shopcraft.product.entity

import com.example.shopcraft.product.dto.ProductResponse
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant

// TODO: Step 1 - Annotate this class as a Jakarta Persistence entity mapped to the products table
// TODO: Step 1 - Annotate the primary key with identity generation
// TODO: Step 1 - Configure column constraints: unique sku, text description, decimal precision for price, non-updatable createdAt
// TODO: Step 1 - Configure string enum mapping for status
class Product(
    val id: Long? = null,
    var sku: String,
    var name: String,
    var description: String? = null,
    var price: BigDecimal,
    var stockQuantity: Int,
    var status: ProductStatus = ProductStatus.ACTIVE,
    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now()
) {
    fun toResponse(): ProductResponse {
        TODO("Step 2 - Map this Product entity properties into an immutable ProductResponse DTO")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Product) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String {
        return "Product(id=$id, sku='$sku', name='$name', price=$price, status=$status)"
    }
}
