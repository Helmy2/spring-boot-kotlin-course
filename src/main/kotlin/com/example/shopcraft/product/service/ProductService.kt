package com.example.shopcraft.product.service

import com.example.shopcraft.common.model.PagedResponse
import com.example.shopcraft.product.dto.ProductFilterCriteria
import com.example.shopcraft.product.dto.ProductPatchRequest
import com.example.shopcraft.product.dto.ProductRequest
import com.example.shopcraft.product.dto.ProductResponse
import org.springframework.data.domain.Pageable

interface ProductService {
    fun createProduct(request: ProductRequest): ProductResponse
    fun getProductById(id: Long): ProductResponse
    fun getAllProducts(): List<ProductResponse>
    fun getProducts(criteria: ProductFilterCriteria, pageable: Pageable): PagedResponse<ProductResponse>
    fun updateProduct(id: Long, request: ProductRequest): ProductResponse
    fun patchProduct(id: Long, request: ProductPatchRequest): ProductResponse
    fun deleteProduct(id: Long)
}
