package com.example.shopcraft.product.service

import com.example.shopcraft.product.dto.ProductRequest
import com.example.shopcraft.product.dto.ProductResponse
import com.example.shopcraft.product.exception.ProductNotFoundException
import com.example.shopcraft.product.repository.ProductRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProductServiceImpl(
    private val productRepository: ProductRepository
) : ProductService {

    @Transactional
    override fun createProduct(request: ProductRequest): ProductResponse {
        val product = request.toEntity()
        val saved = productRepository.save(product)
        return saved.toResponse()
    }

    override fun getProductById(id: Long): ProductResponse {
        val product = productRepository.findByIdOrNull(id)
            ?: throw ProductNotFoundException(id)
        return product.toResponse()
    }

    override fun getAllProducts(): List<ProductResponse> {
        return productRepository.findAll().map { it.toResponse() }
    }
}
