package com.example.shopcraft.audit.aspect

import com.example.shopcraft.audit.annotation.TrackExecutionTime
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.Signature
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ExecutionTimeAspectTest {

    private lateinit var aspect: ExecutionTimeAspect
    private lateinit var joinPoint: ProceedingJoinPoint
    private lateinit var signature: Signature

    @BeforeEach
    fun setUp() {
        aspect = ExecutionTimeAspect()
        joinPoint = mock()
        signature = mock()
        whenever(signature.declaringType).thenReturn(String::class.java)
        whenever(signature.name).thenReturn("testMethod")
        whenever(joinPoint.signature).thenReturn(signature)
    }

    @Test
    fun `given method execution within threshold, when aspect invoked, then proceeds and returns result`() {
        whenever(joinPoint.proceed()).thenReturn("success")
        val annotation = mock<TrackExecutionTime>()
        whenever(annotation.thresholdMs).thenReturn(500L)

        val result = aspect.measureExecutionTime(joinPoint, annotation)

        assertEquals("success", result)
        verify(joinPoint).proceed()
    }

    @Test
    fun `given method execution exceeding threshold, when aspect invoked, then proceeds and returns result`() {
        whenever(joinPoint.proceed()).thenAnswer {
            Thread.sleep(20)
            "slow-result"
        }
        val annotation = mock<TrackExecutionTime>()
        whenever(annotation.thresholdMs).thenReturn(5L)

        val result = aspect.measureExecutionTime(joinPoint, annotation)

        assertEquals("slow-result", result)
        verify(joinPoint).proceed()
    }
}
