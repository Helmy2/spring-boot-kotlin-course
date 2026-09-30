package com.example.shopcraft.audit.repository

import com.example.shopcraft.audit.entity.AuditEvent
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface AuditEventRepository : JpaRepository<AuditEvent, Long> {
    fun findAllByOrderByTimestampDesc(pageable: Pageable): Page<AuditEvent>
    fun findByResourceType(resourceType: String, pageable: Pageable): Page<AuditEvent>
}
