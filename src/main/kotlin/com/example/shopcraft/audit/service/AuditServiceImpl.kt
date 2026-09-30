package com.example.shopcraft.audit.service

import com.example.shopcraft.audit.dto.AuditEventResponse
import com.example.shopcraft.audit.repository.AuditEventRepository
import com.example.shopcraft.common.model.PagedResponse
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuditServiceImpl(
    private val auditEventRepository: AuditEventRepository
) : AuditService {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun recordEvent(
        action: String,
        resourceType: String,
        resourceId: String?,
        details: String?,
        principal: String
    ): AuditEventResponse {
        TODO("Step 2 - Instantiate and persist an AuditEvent with the specified parameters using Propagation.REQUIRES_NEW, and return its DTO")
    }

    override fun getAuditEvents(pageable: Pageable): PagedResponse<AuditEventResponse> {
        TODO("Step 3 - Query auditEventRepository for all audit events ordered by timestamp descending, and map to a PagedResponse envelope")
    }
}
