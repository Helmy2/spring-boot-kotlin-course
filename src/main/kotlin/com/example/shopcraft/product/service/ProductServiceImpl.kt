package com.example.shopcraft.product.service

import com.example.shopcraft.product.dto.ProductRequest
import com.example.shopcraft.product.dto.ProductResponse
import com.example.shopcraft.product.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProductServiceImpl(
    private val productRepository: ProductRepository
) : ProductService {

    @Transactional
    override fun createProduct(request: ProductRequest): ProductResponse {
        TODO("Step 4 - Map the incoming product request to a new Product entity and persist it using the repository, then return the response DTO")
    }

    override fun getProductById(id: Long): ProductResponse {
        TODO("Step 5 - Retrieve the product by ID using the Spring Data Kotlin findByIdOrNull extension function, or throw ProductNotFoundException if not found, then map it to a response DTO")
    }

    override fun getAllProducts(): List<ProductResponse> {
        TODO("Step 6 - Retrieve all products from the repository and map each entity to a response DTO")
    }
}
