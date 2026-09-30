package com.example.shopcraft.common.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ProblemDetail> {
        TODO("Step 5 - Extract FieldErrorDetail list from bindingResult.fieldErrors, construct RFC 7807 ProblemDetail with 400 Bad Request status, title 'Validation Failed', timestamp, and errors list")
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(ex: ResourceNotFoundException): ResponseEntity<ProblemDetail> {
        TODO("Step 6 - Construct RFC 7807 ProblemDetail with 404 Not Found status, title 'Resource Not Found', exception message detail, and timestamp")
    }

    @ExceptionHandler(DuplicateResourceException::class)
    fun handleDuplicateResourceException(ex: DuplicateResourceException): ResponseEntity<ProblemDetail> {
        TODO("Step 7 - Construct RFC 7807 ProblemDetail with 409 Conflict status, title 'Resource Conflict', exception message detail, and timestamp")
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMessageNotReadableException(ex: HttpMessageNotReadableException): ResponseEntity<ProblemDetail> {
        TODO("Step 8 - Construct RFC 7807 ProblemDetail with 400 Bad Request status, title 'Malformed Request Body', and timestamp")
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred. Please contact support."
        ).apply {
            title = "Internal Server Error"
            type = URI.create("https://shopcraft.example.com/errors/internal-server-error")
            setProperty("timestamp", Instant.now())
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail)
    }
}
