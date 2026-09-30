package com.example.shopcraft.common.exception

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException

class GlobalExceptionHandlerTest {

    private lateinit var handler: GlobalExceptionHandler

    @BeforeEach
    fun setUp() {
        handler = GlobalExceptionHandler()
    }

    @Test
    fun `given resource not found exception, when handled, then returns 404 problem detail`() {
        val exception = ResourceNotFoundException("Product with id '99' was not found")

        val response = handler.handleResourceNotFoundException(exception)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("Resource Not Found", response.body?.title)
        assertEquals("Product with id '99' was not found", response.body?.detail)
        assertNotNull(response.body?.properties?.get("timestamp"))
    }

    @Test
    fun `given duplicate resource exception, when handled, then returns 409 problem detail`() {
        val exception = DuplicateResourceException("Product with SKU 'SKU-ABC-1234' already exists")

        val response = handler.handleDuplicateResourceException(exception)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals("Resource Conflict", response.body?.title)
        assertEquals("Product with SKU 'SKU-ABC-1234' already exists", response.body?.detail)
        assertNotNull(response.body?.properties?.get("timestamp"))
    }

    @Test
    fun `given malformed http message exception, when handled, then returns 400 problem detail`() {
        val exception = mock<HttpMessageNotReadableException>()

        val response = handler.handleMessageNotReadableException(exception)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Malformed Request Body", response.body?.title)
        assertNotNull(response.body?.properties?.get("timestamp"))
    }

    @Test
    fun `given general unhandled exception, when handled, then returns 500 problem detail`() {
        val exception = RuntimeException("Unexpected database failure")

        val response = handler.handleGeneralException(exception)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals("Internal Server Error", response.body?.title)
        assertEquals("An unexpected error occurred. Please contact support.", response.body?.detail)
        assertNotNull(response.body?.properties?.get("timestamp"))
    }
}
