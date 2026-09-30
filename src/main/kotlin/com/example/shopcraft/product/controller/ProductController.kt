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

    // TODO: Step 10 - Annotate this method with @GetMapping
    // TODO: Step 10 - Call the service to search products using filter criteria and pagination, and return a 200 OK response with the PagedResponse envelope
    fun getProducts(
        criteria: ProductFilterCriteria,
        pageable: Pageable
    ): ResponseEntity<PagedResponse<ProductResponse>> {
        TODO("Step 10 - Annotate with @GetMapping, search products via service with criteria and pagination, and return 200 OK response with PagedResponse envelope")
    }

    // TODO: Step 7 - Annotate this method with @PutMapping("/{id}")
    // TODO: Step 7 - Annotate the id parameter with @PathVariable and request with @RequestBody
    // TODO: Step 7 - Call the service to update the product by ID, and return a 200 OK response with the updated product body
    fun updateProduct(
        id: Long,
        request: ProductRequest
    ): ResponseEntity<ProductResponse> {
        TODO("Step 7 - Annotate with @PutMapping(\"/{id}\"), @PathVariable, and @RequestBody, update product via service, and return 200 OK response")
    }

    // TODO: Step 8 - Annotate this method with @PatchMapping("/{id}")
    // TODO: Step 8 - Annotate the id parameter with @PathVariable and request with @RequestBody
    // TODO: Step 8 - Call the service to patch the product by ID, and return a 200 OK response with the patched product body
    fun patchProduct(
        id: Long,
        request: ProductPatchRequest
    ): ResponseEntity<ProductResponse> {
        TODO("Step 8 - Annotate with @PatchMapping(\"/{id}\"), @PathVariable, and @RequestBody, patch product via service, and return 200 OK response")
    }

    // TODO: Step 9 - Annotate this method with @DeleteMapping("/{id}")
    // TODO: Step 9 - Annotate the id parameter with @PathVariable
    // TODO: Step 9 - Call the service to delete the product by ID, and return a 204 No Content response with an empty body
    fun deleteProduct(
        id: Long
    ): ResponseEntity<Void> {
        TODO("Step 9 - Annotate with @DeleteMapping(\"/{id}\") and @PathVariable, delete product via service, and return 204 No Content response")
    }
}
