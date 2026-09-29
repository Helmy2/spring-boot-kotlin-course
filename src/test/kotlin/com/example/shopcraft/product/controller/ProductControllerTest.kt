package com.example.shopcraft.product.controller

import com.example.shopcraft.common.config.SecurityConfig
import com.example.shopcraft.product.dto.ProductRequest
import com.example.shopcraft.product.dto.ProductResponse
import com.example.shopcraft.product.entity.ProductStatus
import com.example.shopcraft.product.service.ProductService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
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
import java.math.BigDecimal
import java.time.Instant

@WebMvcTest(ProductController::class)
@Import(SecurityConfig::class)
class ProductControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var productService: ProductService

    @Test
    fun `given valid product request, when POST api v1 products, then returns 201 Created with Location header`() {
        val sampleResponse = ProductResponse(
            id = 100L,
            sku = "SKU-KEY-0001",
            name = "Mechanical Keyboard",
            description = "RGB tactile switches",
            price = BigDecimal("129.99"),
            stockQuantity = 50,
            status = ProductStatus.ACTIVE,
            createdAt = Instant.parse("2026-01-01T12:00:00Z"),
            updatedAt = Instant.parse("2026-01-01T12:00:00Z")
        )
        whenever(productService.createProduct(any<ProductRequest>())).thenReturn(sampleResponse)

        val requestJson = """
            {
                "sku": "SKU-KEY-0001",
                "name": "Mechanical Keyboard",
                "description": "RGB tactile switches",
                "price": 129.99,
                "stockQuantity": 50,
                "status": "ACTIVE"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "/api/v1/products/100"))
            .andExpect(jsonPath("$.id").value(100))
            .andExpect(jsonPath("$.sku").value("SKU-KEY-0001"))
            .andExpect(jsonPath("$.name").value("Mechanical Keyboard"))
            .andExpect(jsonPath("$.price").value(129.99))

        verify(productService).createProduct(any<ProductRequest>())
    }

    @Test
    fun `given existing product id, when GET api v1 products by id, then returns 200 OK and product payload`() {
        val sampleResponse = ProductResponse(
            id = 42L,
            sku = "SKU-MOU-0002",
            name = "Ergonomic Mouse",
            description = "Wireless precision mouse",
            price = BigDecimal("79.99"),
            stockQuantity = 100,
            status = ProductStatus.ACTIVE,
            createdAt = Instant.parse("2026-01-01T12:00:00Z"),
            updatedAt = Instant.parse("2026-01-01T12:00:00Z")
        )
        whenever(productService.getProductById(42L)).thenReturn(sampleResponse)

        mockMvc.perform(get("/api/v1/products/42"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(42))
            .andExpect(jsonPath("$.sku").value("SKU-MOU-0002"))
            .andExpect(jsonPath("$.name").value("Ergonomic Mouse"))
            .andExpect(jsonPath("$.price").value(79.99))

        verify(productService).getProductById(42L)
    }

    @Test
    fun `given products exist, when GET api v1 products, then returns 200 OK and product list`() {
        val productList = listOf(
            ProductResponse(
                id = 1L,
                sku = "SKU-001",
                name = "Item 1",
                description = null,
                price = BigDecimal("15.50"),
                stockQuantity = 10,
                status = ProductStatus.ACTIVE,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        )
        whenever(productService.getAllProducts()).thenReturn(productList)

        mockMvc.perform(get("/api/v1/products"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].sku").value("SKU-001"))

        verify(productService).getAllProducts()
    }
}
