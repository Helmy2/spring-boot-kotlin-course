package com.example.shopcraft.audit.repository

import com.example.shopcraft.audit.entity.AuditEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.data.domain.PageRequest
import java.time.Instant

@DataJpaTest
class AuditEventRepositoryTest {

    @Autowired
    private lateinit var auditEventRepository: AuditEventRepository

    @Test
    fun `given persisted audit events, when findAllByOrderByTimestampDesc, then returns events in descending order`() {
        val event1 = auditEventRepository.save(
            AuditEvent(
                action = "PRODUCT_CREATED",
                resourceType = "PRODUCT",
                resourceId = "1",
                timestamp = Instant.parse("2026-01-01T10:00:00Z")
            )
        )
        val event2 = auditEventRepository.save(
            AuditEvent(
                action = "PRODUCT_UPDATED",
                resourceType = "PRODUCT",
                resourceId = "1",
                timestamp = Instant.parse("2026-01-01T12:00:00Z")
            )
        )

        val page = auditEventRepository.findAllByOrderByTimestampDesc(PageRequest.of(0, 10))

        assertEquals(2, page.content.size)
        assertEquals(event2.id, page.content[0].id)
        assertEquals(event1.id, page.content[1].id)
    }

    @Test
    fun `given persisted audit events, when findByResourceType, then filters by resource type`() {
        auditEventRepository.save(
            AuditEvent(
                action = "PRODUCT_CREATED",
                resourceType = "PRODUCT",
                resourceId = "1"
            )
        )
        auditEventRepository.save(
            AuditEvent(
                action = "USER_REGISTERED",
                resourceType = "USER",
                resourceId = "10"
            )
        )

        val page = auditEventRepository.findByResourceType("PRODUCT", PageRequest.of(0, 10))

        assertEquals(1, page.content.size)
        assertEquals("PRODUCT", page.content[0].resourceType)
        assertEquals("PRODUCT_CREATED", page.content[0].action)
    }
}
