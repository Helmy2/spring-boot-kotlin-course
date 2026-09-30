package com.example.shopcraft.product.service

import com.example.shopcraft.common.model.PagedResponse
import com.example.shopcraft.product.dto.ProductFilterCriteria
import com.example.shopcraft.product.dto.ProductPatchRequest
import com.example.shopcraft.product.dto.ProductRequest
import com.example.shopcraft.product.dto.ProductResponse
import com.example.shopcraft.product.exception.ProductNotFoundException
import com.example.shopcraft.product.repository.ProductRepository
import org.springframework.data.domain.Pageable
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

    override fun getProducts(criteria: ProductFilterCriteria, pageable: Pageable): PagedResponse<ProductResponse> {
        TODO("Step 6 - Generate dynamic specifications from the criteria, query the repository with pagination, and map the resulting Page to a PagedResponse DTO")
    }

    @Transactional
    override fun updateProduct(id: Long, request: ProductRequest): ProductResponse {
        TODO("Step 3 - Retrieve the product entity by ID or throw ProductNotFoundException, replace all mutable fields with values from the request, update the timestamp, save and return the response DTO")
    }

    @Transactional
    override fun patchProduct(id: Long, request: ProductPatchRequest): ProductResponse {
        TODO("Step 4 - Retrieve the product entity by ID or throw ProductNotFoundException, selectively update only non-null fields provided in the patch request, update the timestamp, save and return the response DTO")
    }

    @Transactional
    override fun deleteProduct(id: Long) {
        TODO("Step 5 - Retrieve the product entity by ID or throw ProductNotFoundException, and delete it from the repository")
    }
}
