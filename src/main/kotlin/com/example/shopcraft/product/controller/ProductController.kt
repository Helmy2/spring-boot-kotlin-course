package com.example.shopcraft.product.controller

import com.example.shopcraft.product.dto.ProductRequest
import com.example.shopcraft.product.dto.ProductResponse
import com.example.shopcraft.product.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private val productService: ProductService
) {

    // TODO: Step 7 - Annotate this method with @PostMapping
    // TODO: Step 7 - Annotate the request parameter with @RequestBody
    // TODO: Step 7 - Call the service to create a product, construct an RFC-compliant Location URI (/api/v1/products/{id}), and return a 201 Created response with the body
    fun createProduct(request: ProductRequest): ResponseEntity<ProductResponse> {
        TODO("Step 7 - Annotate with @PostMapping and @RequestBody, create product via service, construct RFC-compliant Location URI, and return 201 Created response")
    }

    // TODO: Step 8 - Annotate this method with @GetMapping("/{id}")
    // TODO: Step 8 - Annotate the id parameter with @PathVariable
    // TODO: Step 8 - Call the service to fetch the product by ID, and return a 200 OK response with the product body
    fun getProductById(id: Long): ResponseEntity<ProductResponse> {
        TODO("Step 8 - Annotate with @GetMapping(\"/{id}\") and @PathVariable, fetch product by ID via service, and return 200 OK response")
    }

    // TODO: Step 9 - Annotate this method with @GetMapping
    // TODO: Step 9 - Call the service to fetch all products, and return a 200 OK response with the list of products
    fun getAllProducts(): ResponseEntity<List<ProductResponse>> {
        TODO("Step 9 - Annotate with @GetMapping, fetch all products via service, and return 200 OK response")
    }
}
