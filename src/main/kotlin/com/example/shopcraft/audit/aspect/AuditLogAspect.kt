package com.example.shopcraft.audit.aspect

import com.example.shopcraft.audit.annotation.AuditLog
import com.example.shopcraft.audit.service.AuditService
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class AuditLogAspect(
    private val auditService: AuditService
) {

    private val log = LoggerFactory.getLogger(AuditLogAspect::class.java)

    @AfterReturning(pointcut = "@annotation(auditLog)", returning = "result")
    fun logSuccessfulOperation(joinPoint: JoinPoint, auditLog: AuditLog, result: Any?) {
        TODO("Step 5 - Extract the resource ID from the result or method arguments, and record the audit event via auditService.recordEvent")
    }
}
