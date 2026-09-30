package com.example.shopcraft.testing.slice.web

import com.example.shopcraft.common.config.SecurityConfig
import com.example.shopcraft.common.exception.GlobalExceptionHandler
import com.example.shopcraft.product.controller.ProductController
import com.example.shopcraft.product.dto.ProductRequest
import com.example.shopcraft.product.exception.ProductNotFoundException
import com.example.shopcraft.product.service.ProductService
import com.example.shopcraft.testing.support.ProductTestDataFactory
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ProductController::class)
@Import(SecurityConfig::class, GlobalExceptionHandler::class)
class ProductControllerWebMvcSliceTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var productService: ProductService

    @Test
    fun `given invalid product payload with blank name, when POST api v1 products, then returns 400 Bad Request with field error`() {
        val invalidPayload = """
            {
                "sku": "SKU-VAL-0001",
                "name": "",
                "price": 29.99,
                "stockQuantity": 10,
                "status": "ACTIVE"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPayload)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.errors[?(@.field == 'name')]").exists())
    }

    @Test
    fun `given valid product request, when POST api v1 products, then returns 201 Created with Location header`() {
        val response = ProductTestDataFactory.createProductResponse(
            id = 42L,
            sku = "SKU-MVC-0042",
            name = "Wireless Gaming Mouse"
        )
        whenever(productService.createProduct(any<ProductRequest>())).thenReturn(response)

        val payload = """
            {
                "sku": "SKU-MVC-0042",
                "name": "Wireless Gaming Mouse",
                "price": 59.99,
                "stockQuantity": 20,
                "status": "ACTIVE"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "/api/v1/products/42"))
            .andExpect(jsonPath("$.id").value(42))
            .andExpect(jsonPath("$.sku").value("SKU-MVC-0042"))
            .andExpect(jsonPath("$.name").value("Wireless Gaming Mouse"))
    }

    @Test
    fun `given non-existing product id, when GET api v1 products by id, then returns 404 Not Found with RFC 7807 problem detail`() {
        whenever(productService.getProductById(999L)).thenThrow(ProductNotFoundException(999L))

        mockMvc.perform(get("/api/v1/products/999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.title").value("Resource Not Found"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.detail").value("Product with id '999' was not found"))
    }
}
