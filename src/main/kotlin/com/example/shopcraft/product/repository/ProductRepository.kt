package com.example.shopcraft.product.repository

import com.example.shopcraft.product.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    fun findBySku(sku: String): Product?
    fun existsBySku(sku: String): Boolean
}
