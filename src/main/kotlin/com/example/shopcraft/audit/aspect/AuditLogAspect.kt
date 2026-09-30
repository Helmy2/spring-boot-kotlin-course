package com.example.shopcraft.audit.aspect

import com.example.shopcraft.audit.annotation.AuditLog
import com.example.shopcraft.audit.service.AuditService
import com.example.shopcraft.product.dto.ProductResponse
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
        val resourceId = extractResourceId(joinPoint, result)
        val methodName = joinPoint.signature.name

        auditService.recordEvent(
            action = auditLog.action,
            resourceType = auditLog.resourceType,
            resourceId = resourceId,
            details = "Method '$methodName' executed successfully"
        )
        log.info("Audit event recorded: action={}, resourceType={}, resourceId={}", auditLog.action, auditLog.resourceType, resourceId)
    }

    private fun extractResourceId(joinPoint: JoinPoint, result: Any?): String? {
        if (result is ProductResponse) {
            return result.id.toString()
        }
        val args = joinPoint.args
        for (arg in args) {
            if (arg is Long) {
                return arg.toString()
            }
        }
        return null
    }
}
