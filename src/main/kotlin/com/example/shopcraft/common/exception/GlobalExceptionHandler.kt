package com.example.shopcraft.common.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(ex: BadCredentialsException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            ex.message ?: "Invalid email or password"
        ).apply {
            title = "Authentication Failed"
            type = URI.create("https://shopcraft.example.com/errors/authentication-failed")
            setProperty("timestamp", Instant.now())
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ProblemDetail> {
        val fieldErrors = ex.bindingResult.fieldErrors.map { fieldError ->
            FieldErrorDetail(
                field = fieldError.field,
                rejectedValue = fieldError.rejectedValue,
                message = fieldError.defaultMessage ?: "Invalid value"
            )
        }

        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Validation failed for ${fieldErrors.size} field(s)"
        ).apply {
            title = "Validation Failed"
            type = URI.create("https://shopcraft.example.com/errors/validation-failed")
            setProperty("timestamp", Instant.now())
            setProperty("errors", fieldErrors)
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail)
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(ex: ResourceNotFoundException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.message ?: "Resource not found"
        ).apply {
            title = "Resource Not Found"
            type = URI.create("https://shopcraft.example.com/errors/not-found")
            setProperty("timestamp", Instant.now())
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail)
    }

    @ExceptionHandler(DuplicateResourceException::class)
    fun handleDuplicateResourceException(ex: DuplicateResourceException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            ex.message ?: "Resource conflict occurred"
        ).apply {
            title = "Resource Conflict"
            type = URI.create("https://shopcraft.example.com/errors/conflict")
            setProperty("timestamp", Instant.now())
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMessageNotReadableException(ex: HttpMessageNotReadableException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Malformed JSON request body or incompatible data type"
        ).apply {
            title = "Malformed Request Body"
            type = URI.create("https://shopcraft.example.com/errors/malformed-request")
            setProperty("timestamp", Instant.now())
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail)
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
