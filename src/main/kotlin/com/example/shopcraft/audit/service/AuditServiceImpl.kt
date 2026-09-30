package com.example.shopcraft.audit.service

import com.example.shopcraft.audit.dto.AuditEventResponse
import com.example.shopcraft.audit.entity.AuditEvent
import com.example.shopcraft.audit.repository.AuditEventRepository
import com.example.shopcraft.common.model.PagedResponse
import com.example.shopcraft.common.model.toPagedResponse
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

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
        val event = AuditEvent(
            action = action,
            resourceType = resourceType,
            resourceId = resourceId,
            details = details,
            principal = principal,
            timestamp = Instant.now()
        )
        val saved = auditEventRepository.save(event)
        return saved.toResponse()
    }

    override fun getAuditEvents(pageable: Pageable): PagedResponse<AuditEventResponse> {
        val page = auditEventRepository.findAllByOrderByTimestampDesc(pageable)
        return page.toPagedResponse { it.toResponse() }
    }
}
