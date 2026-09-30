package com.example.shopcraft.common.model

import org.springframework.data.domain.Page

data class PagedResponse<T : Any>(
    val content: List<T>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)

fun <T : Any, R : Any> Page<T>.toPagedResponse(transform: (T) -> R): PagedResponse<R> = PagedResponse(
    content = content.map(transform),
    pageNumber = number,
    pageSize = size,
    totalElements = totalElements,
    totalPages = totalPages,
    isFirst = isFirst,
    isLast = isLast,
    hasNext = hasNext(),
    hasPrevious = hasPrevious()
)

fun <T : Any> Page<T>.toPagedResponse(): PagedResponse<T> = toPagedResponse { it }
