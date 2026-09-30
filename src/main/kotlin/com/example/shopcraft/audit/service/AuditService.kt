package com.example.shopcraft.audit.service

import com.example.shopcraft.audit.dto.AuditEventResponse
import com.example.shopcraft.common.model.PagedResponse
import org.springframework.data.domain.Pageable

interface AuditService {
    fun recordEvent(
        action: String,
        resourceType: String,
        resourceId: String?,
        details: String? = null,
        principal: String = "system"
    ): AuditEventResponse

    fun getAuditEvents(pageable: Pageable): PagedResponse<AuditEventResponse>
}
