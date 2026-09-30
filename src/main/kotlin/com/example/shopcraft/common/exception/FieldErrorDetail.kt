package com.example.shopcraft.common.exception

data class FieldErrorDetail(
    val field: String,
    val rejectedValue: Any?,
    val message: String
)
