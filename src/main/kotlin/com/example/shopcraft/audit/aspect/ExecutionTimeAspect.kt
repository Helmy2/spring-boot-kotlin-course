package com.example.shopcraft.audit.aspect

import com.example.shopcraft.audit.annotation.TrackExecutionTime
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class ExecutionTimeAspect {

    private val log = LoggerFactory.getLogger(ExecutionTimeAspect::class.java)

    @Around("@annotation(trackExecutionTime)")
    fun measureExecutionTime(joinPoint: ProceedingJoinPoint, trackExecutionTime: TrackExecutionTime): Any? {
        val start = System.currentTimeMillis()
        try {
            return joinPoint.proceed()
        } finally {
            val duration = System.currentTimeMillis() - start
            val declaringType = joinPoint.signature.declaringType.simpleName
            val methodName = joinPoint.signature.name
            val methodSignature = "$declaringType.$methodName"

            if (duration > trackExecutionTime.thresholdMs) {
                log.warn("Performance warning: {} executed in {}ms (threshold: {}ms)", methodSignature, duration, trackExecutionTime.thresholdMs)
            } else {
                log.debug("{} executed in {}ms", methodSignature, duration)
            }
        }
    }
}
