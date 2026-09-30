package com.example.shopcraft.audit

import com.example.shopcraft.audit.repository.AuditEventRepository
import com.example.shopcraft.product.dto.ProductRequest
import com.example.shopcraft.product.entity.ProductStatus
import com.example.shopcraft.product.service.ProductService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@SpringBootTest
class AuditIntegrationTest {

    @Autowired
    private lateinit var productService: ProductService

    @Autowired
    private lateinit var auditEventRepository: AuditEventRepository

    @Test
    fun `given product creation request, when createProduct called, then audit event is automatically recorded via aspect`() {
        val initialCount = auditEventRepository.count()
        val request = ProductRequest(
            sku = "SKU-AOP-0001",
            name = "AOP Monitored Product",
            description = "Tested with AOP aspect",
            price = BigDecimal("199.99"),
            stockQuantity = 25,
            status = ProductStatus.ACTIVE
        )

        val created = productService.createProduct(request)

        val events = auditEventRepository.findAll()
        assertTrue(events.size > initialCount.toInt())
        val matchingEvent = events.find { it.resourceId == created.id.toString() }
        assertEquals("PRODUCT_CREATED", matchingEvent?.action)
        assertEquals("PRODUCT", matchingEvent?.resourceType)
    }
}
