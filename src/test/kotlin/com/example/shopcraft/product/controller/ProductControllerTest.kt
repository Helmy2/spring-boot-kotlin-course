package com.example.shopcraft.product.controller

import com.example.shopcraft.common.config.SecurityConfig
import com.example.shopcraft.common.exception.DuplicateResourceException
import com.example.shopcraft.common.exception.GlobalExceptionHandler
import com.example.shopcraft.common.model.PagedResponse
import com.example.shopcraft.product.dto.ProductFilterCriteria
import com.example.shopcraft.product.dto.ProductPatchRequest
import com.example.shopcraft.product.dto.ProductRequest
import com.example.shopcraft.product.dto.ProductResponse
import com.example.shopcraft.product.entity.ProductStatus
import com.example.shopcraft.product.exception.ProductNotFoundException
import com.example.shopcraft.product.service.ProductService
import org.hamcrest.Matchers.hasItem
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
import org.springframework.security.test.context.support.WithMockUser
import java.math.BigDecimal
import java.time.Instant

@WebMvcTest(ProductController::class)
@Import(SecurityConfig::class, GlobalExceptionHandler::class)
@WithMockUser(roles = ["ADMIN"])
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
    fun `given invalid product request, when POST api v1 products, then returns 400 Bad Request with ProblemDetail errors`() {
        val invalidRequestJson = """
            {
                "sku": "invalid-sku-format",
                "name": "",
                "price": -10.00,
                "stockQuantity": -5
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.title").value("Validation Failed"))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.errors").isArray)
            .andExpect(jsonPath("$.errors[*].field", hasItem("name")))
            .andExpect(jsonPath("$.errors[*].field", hasItem("sku")))
            .andExpect(jsonPath("$.errors[*].field", hasItem("price")))
            .andExpect(jsonPath("$.errors[*].field", hasItem("stockQuantity")))
    }

    @Test
    fun `given duplicate sku, when POST api v1 products, then returns 409 Conflict with ProblemDetail`() {
        whenever(productService.createProduct(any<ProductRequest>()))
            .thenThrow(DuplicateResourceException("Product with SKU 'SKU-DUP-0001' already exists"))

        val requestJson = """
            {
                "sku": "SKU-DUP-0001",
                "name": "Duplicate Product",
                "price": 49.99,
                "stockQuantity": 10
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.title").value("Resource Conflict"))
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.detail").value("Product with SKU 'SKU-DUP-0001' already exists"))
            .andExpect(jsonPath("$.timestamp").exists())
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
    fun `given non-existing product id, when GET api v1 products by id, then returns 404 Not Found with ProblemDetail`() {
        whenever(productService.getProductById(999L)).thenThrow(ProductNotFoundException(999L))

        mockMvc.perform(get("/api/v1/products/999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.title").value("Resource Not Found"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.detail").value("Product with id '999' was not found"))
            .andExpect(jsonPath("$.timestamp").exists())

        verify(productService).getProductById(999L)
    }

    @Test
    fun `given valid update request, when PUT api v1 products by id, then returns 200 OK and updated product`() {
        val updatedResponse = ProductResponse(
            id = 100L,
            sku = "SKU-UPD-0001",
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
                "sku": "SKU-UPD-0001",
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
            .andExpect(jsonPath("$.sku").value("SKU-UPD-0001"))
            .andExpect(jsonPath("$.name").value("Updated Keyboard"))
            .andExpect(jsonPath("$.price").value(149.99))

        verify(productService).updateProduct(eq(100L), any<ProductRequest>())
    }

    @Test
    fun `given valid patch request, when PATCH api v1 products by id, then returns 200 OK and patched product`() {
        val patchedResponse = ProductResponse(
            id = 100L,
            sku = "SKU-KEY-0001",
            name = "Patched Name",
            description = null,
            price = BigDecimal("139.99"),
            stockQuantity = 45,
            status = ProductStatus.ACTIVE,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        whenever(productService.patchProduct(eq(100L), any<ProductPatchRequest>())).thenReturn(patchedResponse)

        val patchJson = """
            {
                "name": "Patched Name",
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
            .andExpect(jsonPath("$.name").value("Patched Name"))
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
    fun `given products exist, when GET api v1 products, then returns 200 OK and product list`() {
        val p1 = ProductResponse(
            id = 1L,
            sku = "SKU-001",
            name = "Product 1",
            description = null,
            price = BigDecimal("10.00"),
            stockQuantity = 5,
            status = ProductStatus.ACTIVE,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        val pagedResponse = PagedResponse(
            content = listOf(p1),
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

        mockMvc.perform(get("/api/v1/products"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].sku").value("SKU-001"))
            .andExpect(jsonPath("$.totalElements").value(1))

        verify(productService).getProducts(any<ProductFilterCriteria>(), any<Pageable>())
    }

    @Test
    fun `given filter and pagination params, when GET api v1 products, then returns 200 OK and PagedResponse envelope`() {
        val pagedResponse = PagedResponse(
            content = emptyList<ProductResponse>(),
            pageNumber = 1,
            pageSize = 5,
            totalElements = 0L,
            totalPages = 0,
            isFirst = false,
            isLast = true,
            hasNext = false,
            hasPrevious = true
        )
        whenever(productService.getProducts(any<ProductFilterCriteria>(), any<Pageable>())).thenReturn(pagedResponse)

        mockMvc.perform(
            get("/api/v1/products")
                .param("search", "phone")
                .param("page", "1")
                .param("size", "5")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.pageNumber").value(1))
            .andExpect(jsonPath("$.pageSize").value(5))
            .andExpect(jsonPath("$.totalElements").value(0))

        verify(productService).getProducts(any<ProductFilterCriteria>(), any<Pageable>())
    }

    @Test
    fun `given malformed json body, when POST api v1 products, then returns 400 Bad Request with ProblemDetail`() {
        val malformedJson = "{ invalid-json: "

        mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.title").value("Malformed Request Body"))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.timestamp").exists())
    }
}
