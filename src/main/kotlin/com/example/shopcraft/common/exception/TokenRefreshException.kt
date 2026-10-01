package com.example.shopcraft.common.exception

class TokenRefreshException(
    val token: String,
    message: String
) : RuntimeException("Failed for [$token]: $message")
