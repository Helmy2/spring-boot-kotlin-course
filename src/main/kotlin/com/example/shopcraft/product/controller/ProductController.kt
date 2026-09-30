package com.example.shopcraft.product.controller

import com.example.shopcraft.common.model.PagedResponse
import com.example.shopcraft.product.dto.ProductFilterCriteria
import com.example.shopcraft.product.dto.ProductPatchRequest
import com.example.shopcraft.product.dto.ProductRequest
import com.example.shopcraft.product.dto.ProductResponse
import com.example.shopcraft.product.service.ProductService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private val productService: ProductService
) {

    @PostMapping
    fun createProduct(@RequestBody request: ProductRequest): ResponseEntity<ProductResponse> {
        val created = productService.createProduct(request)
        val location = URI.create("/api/v1/products/${created.id}")
        return ResponseEntity.created(location).body(created)
    }

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Long): ResponseEntity<ProductResponse> {
        val product = productService.getProductById(id)
        return ResponseEntity.ok(product)
    }

    @GetMapping
    fun getProducts(
        criteria: ProductFilterCriteria,
        pageable: Pageable
    ): ResponseEntity<PagedResponse<ProductResponse>> {
        val pagedProducts = productService.getProducts(criteria, pageable)
        return ResponseEntity.ok(pagedProducts)
    }

    @PutMapping("/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @RequestBody request: ProductRequest
    ): ResponseEntity<ProductResponse> {
        val updated = productService.updateProduct(id, request)
        return ResponseEntity.ok(updated)
    }

    @PatchMapping("/{id}")
    fun patchProduct(
        @PathVariable id: Long,
        @RequestBody request: ProductPatchRequest
    ): ResponseEntity<ProductResponse> {
        val updated = productService.patchProduct(id, request)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<Void> {
        productService.deleteProduct(id)
        return ResponseEntity.noContent().build()
    }
}
