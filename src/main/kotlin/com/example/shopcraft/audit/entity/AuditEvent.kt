package com.example.shopcraft.audit.entity

import com.example.shopcraft.audit.dto.AuditEventResponse
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "audit_events")
class AuditEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var action: String = "",

    @Column(nullable = false)
    var resourceType: String = "",

    @Column(nullable = true)
    var resourceId: String? = null,

    @Column(nullable = false)
    var principal: String = "system",

    @Column(nullable = false)
    var timestamp: Instant = Instant.now(),

    @Column(length = 2000, nullable = true)
    var details: String? = null
) {
    fun toResponse(): AuditEventResponse = AuditEventResponse(
        id = id ?: 0L,
        action = action,
        resourceType = resourceType,
        resourceId = resourceId,
        principal = principal,
        timestamp = timestamp,
        details = details
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AuditEvent) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String {
        return "AuditEvent(id=$id, action='$action', resourceType='$resourceType', resourceId=$resourceId, principal='$principal', timestamp=$timestamp)"
    }
}
