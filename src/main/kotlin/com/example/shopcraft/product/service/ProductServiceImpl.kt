package com.example.shopcraft.product.service

import com.example.shopcraft.common.exception.DuplicateResourceException
import com.example.shopcraft.common.model.PagedResponse
import com.example.shopcraft.common.model.toPagedResponse
import com.example.shopcraft.product.dto.ProductFilterCriteria
import com.example.shopcraft.product.dto.ProductPatchRequest
import com.example.shopcraft.product.dto.ProductRequest
import com.example.shopcraft.product.dto.ProductResponse
import com.example.shopcraft.product.exception.ProductNotFoundException
import com.example.shopcraft.product.repository.ProductRepository
import com.example.shopcraft.product.repository.ProductSpecifications
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
@Transactional(readOnly = true)
class ProductServiceImpl(
    private val productRepository: ProductRepository
) : ProductService {

    @Transactional
    override fun createProduct(request: ProductRequest): ProductResponse {
        // TODO: Step 3 - Check if product with SKU already exists in repository, and throw DuplicateResourceException if it does
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
        val spec = ProductSpecifications.withFilter(criteria)
        val page = productRepository.findAll(spec, pageable)
        return page.toPagedResponse { it.toResponse() }
    }

    @Transactional
    override fun updateProduct(id: Long, request: ProductRequest): ProductResponse {
        val product = productRepository.findByIdOrNull(id)
            ?: throw ProductNotFoundException(id)

        // TODO: Step 4 - Check if another product with the target SKU exists in repository, and throw DuplicateResourceException if it does

        product.sku = request.sku
        product.name = request.name
        product.description = request.description
        product.price = request.price
        product.stockQuantity = request.stockQuantity
        product.status = request.status
        product.updatedAt = Instant.now()

        val updated = productRepository.save(product)
        return updated.toResponse()
    }

    @Transactional
    override fun patchProduct(id: Long, request: ProductPatchRequest): ProductResponse {
        val product = productRepository.findByIdOrNull(id)
            ?: throw ProductNotFoundException(id)

        request.name?.let { product.name = it }
        request.description?.let { product.description = it }
        request.price?.let { product.price = it }
        request.stockQuantity?.let { product.stockQuantity = it }
        request.status?.let { product.status = it }
        product.updatedAt = Instant.now()

        val updated = productRepository.save(product)
        return updated.toResponse()
    }

    @Transactional
    override fun deleteProduct(id: Long) {
        val product = productRepository.findByIdOrNull(id)
            ?: throw ProductNotFoundException(id)
        productRepository.delete(product)
    }
}
