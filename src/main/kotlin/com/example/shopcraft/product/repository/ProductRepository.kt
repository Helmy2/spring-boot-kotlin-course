package com.example.shopcraft.product.repository

import com.example.shopcraft.product.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    fun findBySku(sku: String): Product?
    fun existsBySku(sku: String): Boolean
}
