package com.example.shopcraft.audit.service

import com.example.shopcraft.audit.entity.AuditEvent
import com.example.shopcraft.audit.repository.AuditEventRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.Instant

class AuditServiceTest {

    private lateinit var repository: AuditEventRepository
    private lateinit var service: AuditService

    @BeforeEach
    fun setUp() {
        repository = mock()
        service = AuditServiceImpl(repository)
    }

    @Test
    fun `given valid audit parameters, when recordEvent, then persists event and returns response`() {
        val savedEvent = AuditEvent(
            id = 1L,
            action = "PRODUCT_CREATED",
            resourceType = "PRODUCT",
            resourceId = "10",
            principal = "admin",
            timestamp = Instant.now(),
            details = "Created item"
        )
        whenever(repository.save(any<AuditEvent>())).thenReturn(savedEvent)

        val result = service.recordEvent(
            action = "PRODUCT_CREATED",
            resourceType = "PRODUCT",
            resourceId = "10",
            details = "Created item",
            principal = "admin"
        )

        assertEquals(1L, result.id)
        assertEquals("PRODUCT_CREATED", result.action)
        assertEquals("PRODUCT", result.resourceType)
        assertEquals("10", result.resourceId)
        assertEquals("admin", result.principal)
        verify(repository).save(any<AuditEvent>())
    }

    @Test
    fun `given audit events in repository, when getAuditEvents, then returns paged response`() {
        val pageable = PageRequest.of(0, 10)
        val event = AuditEvent(
            id = 5L,
            action = "PRODUCT_UPDATED",
            resourceType = "PRODUCT",
            resourceId = "20",
            principal = "system",
            timestamp = Instant.now(),
            details = null
        )
        val page = PageImpl(listOf(event), pageable, 1L)
        whenever(repository.findAllByOrderByTimestampDesc(pageable)).thenReturn(page)

        val result = service.getAuditEvents(pageable)

        assertEquals(1, result.content.size)
        assertEquals("PRODUCT_UPDATED", result.content[0].action)
        assertEquals(1L, result.totalElements)
        verify(repository).findAllByOrderByTimestampDesc(pageable)
    }
}
