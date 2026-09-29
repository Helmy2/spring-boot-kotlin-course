package com.example.shopcraft.product.service

import com.example.shopcraft.product.dto.ProductRequest
import com.example.shopcraft.product.dto.ProductResponse

interface ProductService {
    fun createProduct(request: ProductRequest): ProductResponse
    fun getProductById(id: Long): ProductResponse
    fun getAllProducts(): List<ProductResponse>
}
