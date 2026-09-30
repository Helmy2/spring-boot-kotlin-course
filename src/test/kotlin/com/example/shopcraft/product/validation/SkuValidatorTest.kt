package com.example.shopcraft.product.validation

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SkuValidatorTest {

    private lateinit var validator: SkuValidator

    @BeforeEach
    fun setUp() {
        validator = SkuValidator()
    }

    @Test
    fun `given valid sku format, when validated, then returns true`() {
        val validSku = "SKU-ELC-1001"

        val isValid = validator.isValid(validSku, null)

        assertTrue(isValid)
    }

    @Test
    fun `given lowercase sku letters, when validated, then returns false`() {
        val lowercaseSku = "sku-elc-1001"

        val isValid = validator.isValid(lowercaseSku, null)

        assertFalse(isValid)
    }

    @Test
    fun `given invalid prefix, when validated, then returns false`() {
        val invalidPrefixSku = "PRD-ELC-1001"

        val isValid = validator.isValid(invalidPrefixSku, null)

        assertFalse(isValid)
    }

    @Test
    fun `given wrong digit count, when validated, then returns false`() {
        val shortDigitsSku = "SKU-ELC-10"

        val isValid = validator.isValid(shortDigitsSku, null)

        assertFalse(isValid)
    }

    @Test
    fun `given null sku, when validated, then returns true`() {
        val nullSku: String? = null

        val isValid = validator.isValid(nullSku, null)

        assertTrue(isValid)
    }
}
