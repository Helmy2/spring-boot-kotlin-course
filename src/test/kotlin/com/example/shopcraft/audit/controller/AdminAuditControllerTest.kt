package com.example.shopcraft.audit.controller

import com.example.shopcraft.audit.dto.AuditEventResponse
import com.example.shopcraft.audit.service.AuditService
import com.example.shopcraft.common.config.SecurityConfig
import com.example.shopcraft.common.exception.GlobalExceptionHandler
import com.example.shopcraft.common.model.PagedResponse
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Pageable
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.security.test.context.support.WithMockUser
import java.time.Instant

@WebMvcTest(AdminAuditController::class)
@Import(SecurityConfig::class, GlobalExceptionHandler::class)
@WithMockUser(roles = ["ADMIN"])
class AdminAuditControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var auditService: AuditService

    @Test
    fun `given audit events exist, when GET api v1 admin audit-logs, then returns 200 OK and paged response`() {
        val event = AuditEventResponse(
            id = 1L,
            action = "PRODUCT_CREATED",
            resourceType = "PRODUCT",
            resourceId = "100",
            principal = "system",
            timestamp = Instant.now(),
            details = "Product created successfully"
        )
        val pagedResponse = PagedResponse(
            content = listOf(event),
            pageNumber = 0,
            pageSize = 10,
            totalElements = 1L,
            totalPages = 1,
            isFirst = true,
            isLast = true,
            hasNext = false,
            hasPrevious = false
        )
        whenever(auditService.getAuditEvents(any<Pageable>())).thenReturn(pagedResponse)

        mockMvc.perform(get("/api/v1/admin/audit-logs"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].action").value("PRODUCT_CREATED"))
            .andExpect(jsonPath("$.content[0].resourceType").value("PRODUCT"))
            .andExpect(jsonPath("$.content[0].resourceId").value("100"))
            .andExpect(jsonPath("$.totalElements").value(1))

        verify(auditService).getAuditEvents(any<Pageable>())
    }
}
