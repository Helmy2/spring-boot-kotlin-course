package com.example.shopcraft.product.controller

import com.example.shopcraft.common.config.SecurityConfig
import com.example.shopcraft.common.model.PagedResponse
import com.example.shopcraft.product.dto.ProductFilterCriteria
import com.example.shopcraft.product.dto.ProductPatchRequest
import com.example.shopcraft.product.dto.ProductRequest
import com.example.shopcraft.product.dto.ProductResponse
import com.example.shopcraft.product.entity.ProductStatus
import com.example.shopcraft.product.service.ProductService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
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
    fun `given valid update request, when PUT api v1 products by id, then returns 200 OK and updated product`() {
        val updatedResponse = ProductResponse(
            id = 100L,
            sku = "SKU-UPDATED",
            name = "Updated Keyboard",
            description = "Updated switches",
            price = BigDecimal("149.99"),
            stockQuantity = 40,
            status = ProductStatus.ACTIVE,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        whenever(productService.updateProduct(eq(100L), any<ProductRequest>())).thenReturn(updatedResponse)

        val requestJson = """
            {
                "sku": "SKU-UPDATED",
                "name": "Updated Keyboard",
                "description": "Updated switches",
                "price": 149.99,
                "stockQuantity": 40,
                "status": "ACTIVE"
            }
        """.trimIndent()

        mockMvc.perform(
            put("/api/v1/products/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(100))
            .andExpect(jsonPath("$.sku").value("SKU-UPDATED"))
            .andExpect(jsonPath("$.name").value("Updated Keyboard"))
            .andExpect(jsonPath("$.price").value(149.99))

        verify(productService).updateProduct(eq(100L), any<ProductRequest>())
    }

    @Test
    fun `given valid patch request, when PATCH api v1 products by id, then returns 200 OK and patched product`() {
        val patchedResponse = ProductResponse(
            id = 100L,
            sku = "SKU-KEY-0001",
            name = "Patched Keyboard",
            description = "RGB tactile switches",
            price = BigDecimal("139.99"),
            stockQuantity = 50,
            status = ProductStatus.ACTIVE,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        whenever(productService.patchProduct(eq(100L), any<ProductPatchRequest>())).thenReturn(patchedResponse)

        val patchJson = """
            {
                "name": "Patched Keyboard",
                "price": 139.99
            }
        """.trimIndent()

        mockMvc.perform(
            patch("/api/v1/products/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchJson)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(100))
            .andExpect(jsonPath("$.name").value("Patched Keyboard"))
            .andExpect(jsonPath("$.price").value(139.99))

        verify(productService).patchProduct(eq(100L), any<ProductPatchRequest>())
    }

    @Test
    fun `given existing product id, when DELETE api v1 products by id, then returns 204 No Content`() {
        mockMvc.perform(delete("/api/v1/products/100"))
            .andExpect(status().isNoContent)

        verify(productService).deleteProduct(100L)
    }

    @Test
    fun `given filter and pagination params, when GET api v1 products, then returns 200 OK and PagedResponse envelope`() {
        val sampleProduct = ProductResponse(
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
        val pagedResponse = PagedResponse(
            content = listOf(sampleProduct),
            pageNumber = 0,
            pageSize = 10,
            totalElements = 1L,
            totalPages = 1,
            isFirst = true,
            isLast = true,
            hasNext = false,
            hasPrevious = false
        )
        whenever(productService.getProducts(any<ProductFilterCriteria>(), any<Pageable>())).thenReturn(pagedResponse)

        mockMvc.perform(get("/api/v1/products?page=0&size=10&search=Item"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].sku").value("SKU-001"))
            .andExpect(jsonPath("$.pageNumber").value(0))
            .andExpect(jsonPath("$.pageSize").value(10))
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.totalPages").value(1))
            .andExpect(jsonPath("$.isFirst").value(true))
            .andExpect(jsonPath("$.isLast").value(true))

        verify(productService).getProducts(any<ProductFilterCriteria>(), any<Pageable>())
    }
}
