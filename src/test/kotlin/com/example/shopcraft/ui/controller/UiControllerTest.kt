package com.example.shopcraft.ui.controller

import com.example.shopcraft.audit.dto.AuditEventResponse
import com.example.shopcraft.audit.service.AuditService
import com.example.shopcraft.common.config.SecurityConfig
import com.example.shopcraft.common.exception.GlobalExceptionHandler
import com.example.shopcraft.common.model.PagedResponse
import com.example.shopcraft.product.dto.ProductResponse
import com.example.shopcraft.product.entity.ProductStatus
import com.example.shopcraft.product.service.ProductService
import org.hamcrest.Matchers.containsString
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.Instant

@WebMvcTest(UiController::class)
@Import(SecurityConfig::class, GlobalExceptionHandler::class)
class UiControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var productService: ProductService

    @MockitoBean
    private lateinit var auditService: AuditService

    private val sampleProduct = ProductResponse(
        id = 1L,
        sku = "SKU-KEY-0001",
        name = "Mechanical Keyboard",
        description = "Tactile mechanical keyboard with RGB backlighting",
        price = BigDecimal("129.99"),
        stockQuantity = 25,
        status = ProductStatus.ACTIVE,
        createdAt = Instant.parse("2026-03-01T10:00:00Z"),
        updatedAt = Instant.parse("2026-03-01T10:00:00Z")
    )

    private val sampleAuditEvent = AuditEventResponse(
        id = 101L,
        action = "PRODUCT_CREATED",
        resourceType = "PRODUCT",
        resourceId = "1",
        principal = "admin@shopcraft.com",
        timestamp = Instant.parse("2026-03-01T10:05:00Z"),
        details = "Created SKU-KEY-0001"
    )

    @Test
    fun `given storefront request, when GET slash, then returns 200 OK with html page containing product cards`() {
        whenever(productService.getAllProducts()).thenReturn(listOf(sampleProduct))

        mockMvc.perform(get("/"))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(containsString("ShopCraft")))
            .andExpect(content().string(containsString("Modern Craftsmanship")))
            .andExpect(content().string(containsString("Mechanical Keyboard")))
            .andExpect(content().string(containsString("SKU-KEY-0001")))
            .andExpect(content().string(containsString("$129.99")))
            .andExpect(content().string(containsString("25 in stock")))
    }

    @Test
    fun `given search and status filters, when GET slash ui slash products, then returns 200 OK with htmx fragment containing matching products`() {
        val pagedResponse = PagedResponse(
            content = listOf(sampleProduct),
            pageNumber = 0,
            pageSize = 100,
            totalElements = 1L,
            totalPages = 1,
            isFirst = true,
            isLast = true,
            hasNext = false,
            hasPrevious = false
        )
        whenever(productService.getProducts(any(), any())).thenReturn(pagedResponse)

        mockMvc.perform(
            get("/ui/products")
                .param("search", "Keyboard")
                .param("status", "ACTIVE")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(containsString("Mechanical Keyboard")))
            .andExpect(content().string(containsString("SKU-KEY-0001")))
            .andExpect(content().string(containsString("ACTIVE")))
    }

    @Test
    fun `given no matching products, when GET slash ui slash products, then returns 200 OK with empty state fragment`() {
        val emptyPagedResponse = PagedResponse<ProductResponse>(
            content = emptyList(),
            pageNumber = 0,
            pageSize = 100,
            totalElements = 0L,
            totalPages = 0,
            isFirst = true,
            isLast = true,
            hasNext = false,
            hasPrevious = false
        )
        whenever(productService.getProducts(any(), any())).thenReturn(emptyPagedResponse)

        mockMvc.perform(
            get("/ui/products")
                .param("search", "NonExistentItem")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(containsString("No products found")))
    }

    @Test
    fun `given admin dashboard request, when GET slash ui slash admin, then returns 200 OK with operations center and metrics`() {
        whenever(productService.getAllProducts()).thenReturn(listOf(sampleProduct))
        val pagedAudits = PagedResponse(
            content = listOf(sampleAuditEvent),
            pageNumber = 0,
            pageSize = 50,
            totalElements = 1L,
            totalPages = 1,
            isFirst = true,
            isLast = true,
            hasNext = false,
            hasPrevious = false
        )
        whenever(auditService.getAuditEvents(any())).thenReturn(pagedAudits)

        mockMvc.perform(get("/ui/admin"))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(containsString("Operations Control Center")))
            .andExpect(content().string(containsString("Inventory Management")))
            .andExpect(content().string(containsString("Security &amp; Telemetry")))
            .andExpect(content().string(containsString("SKU-KEY-0001")))
            .andExpect(content().string(containsString("PRODUCT_CREATED")))
            .andExpect(content().string(containsString("admin@shopcraft.com")))
    }

    @Test
    fun `given audit telemetry request, when GET slash ui slash audit, then returns 200 OK with htmx fragment containing audit event cards`() {
        val pagedAudits = PagedResponse(
            content = listOf(sampleAuditEvent),
            pageNumber = 0,
            pageSize = 50,
            totalElements = 1L,
            totalPages = 1,
            isFirst = true,
            isLast = true,
            hasNext = false,
            hasPrevious = false
        )
        whenever(auditService.getAuditEvents(any())).thenReturn(pagedAudits)

        mockMvc.perform(get("/ui/audit"))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(containsString("PRODUCT_CREATED")))
            .andExpect(content().string(containsString("admin@shopcraft.com")))
            .andExpect(content().string(containsString("PRODUCT:1")))
    }
}
