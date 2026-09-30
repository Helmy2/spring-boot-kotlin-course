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
        TODO("Step 4 - Measure execution time around joinPoint.proceed(), log a warning if duration exceeds trackExecutionTime.thresholdMs, and return the execution result")
    }
}
