package com.example.shopcraft.common.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class PagedResponseTest {

    @Test
    fun `given spring data page, when toPagedResponse, then correctly populates pagination metadata`() {
        val pageable = PageRequest.of(0, 2)
        val items = listOf("item1", "item2")
        val page = PageImpl(items, pageable, 5L)

        val pagedResponse = page.toPagedResponse()

        assertThat(pagedResponse.content).containsExactly("item1", "item2")
        assertThat(pagedResponse.pageNumber).isEqualTo(0)
        assertThat(pagedResponse.pageSize).isEqualTo(2)
        assertThat(pagedResponse.totalElements).isEqualTo(5L)
        assertThat(pagedResponse.totalPages).isEqualTo(3)
        assertThat(pagedResponse.isFirst).isTrue()
        assertThat(pagedResponse.isLast).isFalse()
        assertThat(pagedResponse.hasNext).isTrue()
        assertThat(pagedResponse.hasPrevious).isFalse()
    }

    @Test
    fun `given spring data page with transform, when toPagedResponse, then maps content elements`() {
        val pageable = PageRequest.of(1, 2)
        val items = listOf(10, 20)
        val page = PageImpl(items, pageable, 4L)

        val pagedResponse = page.toPagedResponse { "NUM-$it" }

        assertThat(pagedResponse.content).containsExactly("NUM-10", "NUM-20")
        assertThat(pagedResponse.pageNumber).isEqualTo(1)
        assertThat(pagedResponse.isFirst).isFalse()
        assertThat(pagedResponse.isLast).isTrue()
        assertThat(pagedResponse.hasNext).isFalse()
        assertThat(pagedResponse.hasPrevious).isTrue()
    }
}
