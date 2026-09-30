package com.example.shopcraft.product.service

import com.example.shopcraft.audit.annotation.AuditLog
import com.example.shopcraft.audit.annotation.TrackExecutionTime
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

    @AuditLog(action = "PRODUCT_CREATED", resourceType = "PRODUCT")
    @Transactional
    override fun createProduct(request: ProductRequest): ProductResponse {
        if (productRepository.existsBySku(request.sku)) {
            throw DuplicateResourceException("Product with SKU '${request.sku}' already exists")
        }
        val product = request.toEntity()
        val saved = productRepository.save(product)
        return saved.toResponse()
    }

    @TrackExecutionTime(thresholdMs = 100)
    override fun getProductById(id: Long): ProductResponse {
        val product = productRepository.findByIdOrNull(id)
            ?: throw ProductNotFoundException(id)
        return product.toResponse()
    }

    @TrackExecutionTime(thresholdMs = 100)
    override fun getAllProducts(): List<ProductResponse> {
        return productRepository.findAll().map { it.toResponse() }
    }

    @TrackExecutionTime(thresholdMs = 100)
    override fun getProducts(criteria: ProductFilterCriteria, pageable: Pageable): PagedResponse<ProductResponse> {
        val spec = ProductSpecifications.withFilter(criteria)
        val page = productRepository.findAll(spec, pageable)
        return page.toPagedResponse { it.toResponse() }
    }

    @AuditLog(action = "PRODUCT_UPDATED", resourceType = "PRODUCT")
    @Transactional
    override fun updateProduct(id: Long, request: ProductRequest): ProductResponse {
        val product = productRepository.findByIdOrNull(id)
            ?: throw ProductNotFoundException(id)

        val existingWithSku = productRepository.findBySku(request.sku)
        if (existingWithSku != null && existingWithSku.id != id) {
            throw DuplicateResourceException("Product with SKU '${request.sku}' already exists")
        }

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

    @AuditLog(action = "PRODUCT_PATCHED", resourceType = "PRODUCT")
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

    @AuditLog(action = "PRODUCT_DELETED", resourceType = "PRODUCT")
    @Transactional
    override fun deleteProduct(id: Long) {
        val product = productRepository.findByIdOrNull(id)
            ?: throw ProductNotFoundException(id)
        productRepository.delete(product)
    }
}
