package com.example.shopcraft.audit.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class TrackExecutionTime(
    val thresholdMs: Long = 250L
)
