package com.example.shopcraft.audit.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AuditLog(
    val action: String,
    val resourceType: String = "PRODUCT"
)
