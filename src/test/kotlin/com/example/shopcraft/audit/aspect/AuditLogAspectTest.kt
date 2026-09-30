package com.example.shopcraft.audit.aspect

import com.example.shopcraft.audit.annotation.AuditLog
import com.example.shopcraft.audit.dto.AuditEventResponse
import com.example.shopcraft.audit.service.AuditService
import com.example.shopcraft.product.dto.ProductResponse
import com.example.shopcraft.product.entity.ProductStatus
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.Signature
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.Instant

class AuditLogAspectTest {

    private lateinit var auditService: AuditService
    private lateinit var aspect: AuditLogAspect
    private lateinit var joinPoint: JoinPoint
    private lateinit var signature: Signature
    private lateinit var auditLog: AuditLog

    @BeforeEach
    fun setUp() {
        auditService = mock()
        aspect = AuditLogAspect(auditService)
        joinPoint = mock()
        signature = mock()
        auditLog = mock()

        whenever(signature.name).thenReturn("sampleMethod")
        whenever(joinPoint.signature).thenReturn(signature)
        whenever(auditLog.action).thenReturn("PRODUCT_CREATED")
        whenever(auditLog.resourceType).thenReturn("PRODUCT")
    }

    @Test
    fun `given method returning product response, when audit log aspect triggered, then records audit event with product id`() {
        val productResponse = ProductResponse(
            id = 42L,
            sku = "SKU-TEST-0001",
            name = "Test Item",
            description = null,
            price = BigDecimal("99.99"),
            stockQuantity = 10,
            status = ProductStatus.ACTIVE,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        val dummyResponse = AuditEventResponse(
            id = 1L,
            action = "PRODUCT_CREATED",
            resourceType = "PRODUCT",
            resourceId = "42",
            principal = "system",
            timestamp = Instant.now(),
            details = "Method 'sampleMethod' executed successfully"
        )
        whenever(auditService.recordEvent(any(), any(), any(), any(), any())).thenReturn(dummyResponse)

        aspect.logSuccessfulOperation(joinPoint, auditLog, productResponse)

        verify(auditService).recordEvent(
            action = eq("PRODUCT_CREATED"),
            resourceType = eq("PRODUCT"),
            resourceId = eq("42"),
            details = eq("Method 'sampleMethod' executed successfully"),
            principal = eq("system")
        )
    }

    @Test
    fun `given void method with id parameter, when audit log aspect triggered, then records audit event with argument id`() {
        whenever(joinPoint.args).thenReturn(arrayOf<Any>(99L))
        whenever(auditLog.action).thenReturn("PRODUCT_DELETED")
        val dummyResponse = AuditEventResponse(
            id = 2L,
            action = "PRODUCT_DELETED",
            resourceType = "PRODUCT",
            resourceId = "99",
            principal = "system",
            timestamp = Instant.now(),
            details = "Method 'sampleMethod' executed successfully"
        )
        whenever(auditService.recordEvent(any(), any(), any(), any(), any())).thenReturn(dummyResponse)

        aspect.logSuccessfulOperation(joinPoint, auditLog, null)

        verify(auditService).recordEvent(
            action = eq("PRODUCT_DELETED"),
            resourceType = eq("PRODUCT"),
            resourceId = eq("99"),
            details = eq("Method 'sampleMethod' executed successfully"),
            principal = eq("system")
        )
    }
}
