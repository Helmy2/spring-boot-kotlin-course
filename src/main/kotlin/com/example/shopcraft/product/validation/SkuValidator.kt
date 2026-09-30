package com.example.shopcraft.product.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class SkuValidator : ConstraintValidator<ValidSku, String?> {

    private val skuRegex = Regex("^SKU-[A-Z]{3}-\\d{4}$")

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return true
        }
        return skuRegex.matches(value)
    }
}
