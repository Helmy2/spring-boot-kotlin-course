package com.example.shopcraft.audit.controller

import com.example.shopcraft.audit.dto.AuditEventResponse
import com.example.shopcraft.audit.service.AuditService
import com.example.shopcraft.common.model.PagedResponse
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/audit-logs")
class AdminAuditController(
    private val auditService: AuditService
) {

    // TODO: Step 7 - Annotate this method with @GetMapping
    // TODO: Step 7 - Call auditService.getAuditEvents(pageable) and return a 200 OK response with the PagedResponse envelope
    fun getAuditLogs(pageable: Pageable): ResponseEntity<PagedResponse<AuditEventResponse>> {
        TODO("Step 7 - Annotate with @GetMapping, call auditService.getAuditEvents(pageable), and return a 200 OK response with the PagedResponse envelope")
    }
}
