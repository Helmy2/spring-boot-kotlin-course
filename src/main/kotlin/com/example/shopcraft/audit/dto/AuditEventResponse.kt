package com.example.shopcraft.audit.dto

import java.time.Instant

data class AuditEventResponse(
    val id: Long,
    val action: String,
    val resourceType: String,
    val resourceId: String?,
    val principal: String,
    val timestamp: Instant,
    val details: String?
)
